package avogadri.marco.localshare.data.p2p

import kotlinx.coroutines.flow.Flow

data class PeerDevice(
    val name: String,
    val address: String,
)

data class P2pConnectionInfo(
    val groupOwnerAddress: String,
    val isGroupOwner: Boolean,
)

interface P2pManager {
    fun observePeers(): Flow<List<PeerDevice>>
    fun startDiscovery()
    fun stopDiscovery()
    fun observeConnectionInfo(): Flow<P2pConnectionInfo?>
    suspend fun connect(peer: PeerDevice)
    fun disconnect()
}
