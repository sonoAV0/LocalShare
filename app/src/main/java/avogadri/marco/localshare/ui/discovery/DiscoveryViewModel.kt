package avogadri.marco.localshare.ui.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avogadri.marco.localshare.data.p2p.P2pManager
import avogadri.marco.localshare.data.p2p.PeerDevice
import avogadri.marco.localshare.data.p2p.TransferSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiscoveryUiState(
    val peers: List<PeerDevice> = emptyList(),
)

class DiscoveryViewModel(private val p2pManager: P2pManager) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {
        p2pManager.startDiscovery()
        viewModelScope.launch {
            p2pManager.observePeers().collect { peers ->
                _uiState.update { it.copy(peers = peers) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        p2pManager.stopDiscovery()
    }

    /**
     * Metodo che controlla che una transazione non sia già in corso
     */
    fun canStartTransfer(): Boolean = !TransferSessionState.isTransferring.get()
}
