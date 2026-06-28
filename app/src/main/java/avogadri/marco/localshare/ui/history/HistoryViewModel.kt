package avogadri.marco.localshare.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(historyRepository: HistoryRepository) : ViewModel() {

    val history: StateFlow<List<HistoryEntity>> = historyRepository.observeMergedHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
