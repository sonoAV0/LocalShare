package avogadri.marco.localshare.data.p2p

import kotlinx.coroutines.flow.Flow

data class PeerDevice(
    val name: String,
    val address: String,
)

interface P2pManager {
    fun observePeers(): Flow<List<PeerDevice>>
    fun startDiscovery()
    fun stopDiscovery()
}
