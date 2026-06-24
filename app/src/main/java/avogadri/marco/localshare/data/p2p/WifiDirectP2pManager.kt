package avogadri.marco.localshare.data.p2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WifiDirectP2pManager(private val context: Context) : P2pManager {

    private val manager: WifiP2pManager =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val p2pChannel: WifiP2pManager.Channel =
        manager.initialize(context, context.mainLooper, null)

    @SuppressLint("MissingPermission")
    override fun observePeers(): Flow<List<PeerDevice>> = callbackFlow {
        fun requestPeers() {
            manager.requestPeers(p2pChannel) { peers ->
                trySend(peers.deviceList.map { it.toPeerDevice() })
            }
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION) {
                    requestPeers()
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        awaitClose { context.unregisterReceiver(receiver) }
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() {
        manager.discoverPeers(p2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() = Unit
            override fun onFailure(reason: Int) = Unit
        })
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() {
        manager.stopPeerDiscovery(p2pChannel, null)
    }
}

private fun WifiP2pDevice.toPeerDevice() = PeerDevice(name = deviceName, address = deviceAddress)
