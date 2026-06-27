package avogadri.marco.localshare.data.p2p

import android.content.Context
import avogadri.marco.localshare.service.TransferForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Classe che avvia una coroutine che si occupa di monitorare gli eventi
 * di rete (come un avvio o ricezione di una comunicazione tra peer)
 *
 * Se il peer che capta il broadcast ed è il ricevente, allora chiama il metodo per preparare la ricezione
 *
 * @param context Context dell'applicazione
 * @param p2pManager Manager di P2P
 */
class IncomingTransferWatcher(
    private val context: Context,
    private val p2pManager: P2pManager,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var started = false

    fun start() {
        // se già avviato, non faccio nulla
        if (started) return
        started = true

        scope.launch {
            p2pManager.observeConnectionInfo().collect { connectionInfo ->
                // controllo che connectionInfo non sia vuoto e che il device non sia quello inviante del file
                if (connectionInfo != null && !TransferSessionState.isTransferring.get()) {
                    TransferForegroundService.startReceive(context, connectionInfo)
                }
            }
        }
    }
}
