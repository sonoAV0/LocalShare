package avogadri.marco.localshare.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    private val remoteEntries = MutableStateFlow<List<HistoryEntity>>(emptyList())

    init {
        viewModelScope.launch {
            remoteEntries.value = historyRepository.fetchGroupHistory()
        }
    }

    val history: StateFlow<List<HistoryEntity>> = combine(
        historyRepository.observeHistory(),
        remoteEntries,
    ) { local, remote ->
        val localTransferIds = local.map { it.transferId }.toSet()
        val remoteOnly = remote.filter { it.transferId !in localTransferIds }
        (local + remoteOnly).sortedByDescending { it.timestampMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
