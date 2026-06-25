package avogadri.marco.localshare.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import avogadri.marco.localshare.data.AppContainer
import avogadri.marco.localshare.data.local.db.TransferDirection
import avogadri.marco.localshare.data.p2p.P2pConnectionInfo
import avogadri.marco.localshare.data.p2p.PeerDevice
import avogadri.marco.localshare.data.p2p.TransferSessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Esegue connessione P2P + trasferimento file fuori dal ciclo di vita di Activity/ViewModel,
 * mostrando una notifica persistente. Avviato in modalità invio (dopo selezione file su una
 * [PeerDevice]) oppure in modalità ricezione (quando il device rileva passivamente una
 * connessione P2P in arrivo che non ha avviato lui stesso).
 */
class TransferForegroundService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(CHANNEL_ID, "Trasferimenti", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    // Metodo richiamato ogni volta che il servizio viene avviato, reindirizza in baso al tipo di peer (send/receive)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SEND -> handleSend(intent)
            ACTION_RECEIVE -> handleReceive(intent)
            else -> stopSelf()
        }
        return START_NOT_STICKY // in caso in cui il servizio si interrompa, questo non viene riavviato
    }

    private fun handleSend(intent: Intent) {
        val peer = PeerDevice(
            name = intent.getStringExtra(EXTRA_PEER_NAME).orEmpty(),
            address = intent.getStringExtra(EXTRA_PEER_ADDRESS).orEmpty(),
        )
        val fileUri = intent.getParcelableExtra(EXTRA_FILE_URI, Uri::class.java) ?: run { stopSelf(); return }

        TransferSessionState.isTransferring.set(true)
        startForeground(NOTIFICATION_ID, buildNotification("Connessione a ${peer.name}…")) // avvio del foreground service

        serviceScope.launch {
            try {
                AppContainer.p2pManager.connect(peer)
                val connectionInfo = AppContainer.p2pManager.observeConnectionInfo().filterNotNull().first()
                updateNotification("Invio in corso a ${peer.name}…")
                val result = AppContainer.transferManager.sendFile(connectionInfo, fileUri)
                val (lat, lon) = getLastLocation() ?: (null to null)
                AppContainer.historyRepository.recordTransfer(
                    peerDeviceId = peer.address,
                    fileName = result.fileName,
                    sizeBytes = result.sizeBytes,
                    direction = TransferDirection.SENT,
                    latitude = lat,
                    longitude = lon,
                )
                updateNotification("Invio completato: ${result.fileName}")
            } catch (e: Exception) {
                updateNotification("Errore durante la condivisione del file")
            } finally {
                TransferSessionState.isTransferring.set(false)
                AppContainer.p2pManager.disconnect() // chiusura del gruppo
                stopForeground(STOP_FOREGROUND_DETACH) // sgancia la notifica dal service
                stopSelf() // chiusura del service
            }
        }
    }

    private fun handleReceive(intent: Intent) {
        val connectionInfo = P2pConnectionInfo(
            groupOwnerAddress = intent.getStringExtra(EXTRA_GROUP_OWNER_ADDRESS).orEmpty(),
            isGroupOwner = intent.getBooleanExtra(EXTRA_IS_GROUP_OWNER, false),
        )

        TransferSessionState.isTransferring.set(true)
        startForeground(NOTIFICATION_ID, buildNotification("In attesa di un file…"))

        serviceScope.launch {
            try {
                val result = AppContainer.transferManager.receiveFile(connectionInfo)
                val (lat, lon) = getLastLocation() ?: (null to null)
                AppContainer.historyRepository.recordTransfer(
                    peerDeviceId = connectionInfo.groupOwnerAddress,
                    fileName = result.fileName,
                    sizeBytes = result.sizeBytes,
                    direction = TransferDirection.RECEIVED,
                    latitude = lat,
                    longitude = lon,
                )
                updateNotification("Ricevuto: ${result.fileName}")
            } catch (e: Exception) {
                updateNotification("Errore durante la condivisione del file")
            } finally {
                TransferSessionState.isTransferring.set(false)
                stopForeground(STOP_FOREGROUND_DETACH)
                stopSelf()
            }
        }
    }

    //------------------------------------
    // METODI DI UTILITY PER LE NOTIFICHE
    //------------------------------------
    private fun buildNotification(text: String): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LocalShare")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(true) // impedisce all'utente di togliere la notifica
            .build()

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, buildNotification(text))
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(): Pair<Double, Double>? {
        val lm = getSystemService(LocationManager::class.java)
        return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .firstNotNullOfOrNull { provider ->
                runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            }?.let { it.latitude to it.longitude }
    }

    // Alla distruzione del service, cancella anche tutte le coroutine in corso nel serviceScope
    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "transfers"
        private const val NOTIFICATION_ID = 1

        private const val ACTION_SEND = "avogadri.marco.localshare.action.SEND"
        private const val ACTION_RECEIVE = "avogadri.marco.localshare.action.RECEIVE"
        private const val EXTRA_PEER_NAME = "peer_name"
        private const val EXTRA_PEER_ADDRESS = "peer_address"
        private const val EXTRA_FILE_URI = "file_uri"
        private const val EXTRA_GROUP_OWNER_ADDRESS = "group_owner_address"
        private const val EXTRA_IS_GROUP_OWNER = "is_group_owner"

        fun startSend(context: Context, peer: PeerDevice, fileUri: Uri) {
            val intent = Intent(context, TransferForegroundService::class.java).apply {
                action = ACTION_SEND
                putExtra(EXTRA_PEER_NAME, peer.name)
                putExtra(EXTRA_PEER_ADDRESS, peer.address)
                putExtra(EXTRA_FILE_URI, fileUri)
            }
            context.startForegroundService(intent)
        }

        fun startReceive(context: Context, connectionInfo: P2pConnectionInfo) {
            val intent = Intent(context, TransferForegroundService::class.java).apply {
                action = ACTION_RECEIVE
                putExtra(EXTRA_GROUP_OWNER_ADDRESS, connectionInfo.groupOwnerAddress)
                putExtra(EXTRA_IS_GROUP_OWNER, connectionInfo.isGroupOwner)
            }
            context.startForegroundService(intent)
        }
    }
}
