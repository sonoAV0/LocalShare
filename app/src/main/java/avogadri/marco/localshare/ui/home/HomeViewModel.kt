package avogadri.marco.localshare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avogadri.marco.localshare.data.local.DeviceIdProvider
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.repository.DeviceRepository
import avogadri.marco.localshare.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val deviceId: String = "",
    val isRegistering: Boolean = true,
    val errorMessage: String? = null,
)

class HomeViewModel(
    deviceIdProvider: DeviceIdProvider,
    private val deviceRepository: DeviceRepository,
    historyRepository: HistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(deviceId = deviceIdProvider.getOrCreateDeviceId()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val recentHistory: StateFlow<List<HistoryEntity>> = historyRepository.observeMergedHistory()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            deviceRepository.registerDevice(_uiState.value.deviceId).fold(
                onSuccess = { _uiState.update { it.copy(isRegistering = false) } },
                onFailure = { e -> _uiState.update { it.copy(isRegistering = false, errorMessage = e.message) } },
            )
        }
    }
}
