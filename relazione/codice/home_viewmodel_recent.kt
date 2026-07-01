val recentHistory: StateFlow<List<HistoryEntity>> =
    historyRepository.observeMergedHistory()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
