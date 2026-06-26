package avogadri.marco.localshare.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import avogadri.marco.localshare.data.local.BackendPreferences
import avogadri.marco.localshare.data.local.DeviceIdProvider
import avogadri.marco.localshare.data.remote.GroupResponse
import avogadri.marco.localshare.data.remote.LocalShareApi
import avogadri.marco.localshare.data.repository.DeviceRepository
import avogadri.marco.localshare.service.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val syncEnabled: Boolean = false,
    val groupCode: String = "",
    val groupActionLoading: Boolean = false,
    val groupActionError: String? = null,
    val backendError: String? = null,
)

class SettingsViewModel(
    application: Application,
    private val prefs: BackendPreferences,
    private val deviceIdProvider: DeviceIdProvider,
    private val deviceRepository: DeviceRepository,
    private val api: LocalShareApi,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            syncEnabled = prefs.syncEnabled,
            groupCode = prefs.groupCode ?: "",
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setSyncEnabled(enabled: Boolean) {
        prefs.syncEnabled = enabled
        _uiState.update { it.copy(syncEnabled = enabled) }
        if (enabled && prefs.jwtToken == null) {
            viewModelScope.launch {
                deviceRepository.registerDevice(deviceIdProvider.getOrCreateDeviceId())
                    .onSuccess { enqueueSyncWorker() }
                    .onFailure {
                        prefs.syncEnabled = false
                        _uiState.update { it.copy(syncEnabled = false, backendError = "Backend non raggiungibile") }
                    }
            }
        } else if (enabled) {
            enqueueSyncWorker()
        }
    }

    private fun enqueueSyncWorker() {
        WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            SyncWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    fun createGroup() {
        viewModelScope.launch {
            _uiState.update { it.copy(groupActionLoading = true, groupActionError = null) }
            runCatching { api.createGroup() }
                .onSuccess { response ->
                    prefs.groupCode = response.code
                    _uiState.update { it.copy(groupActionLoading = false, groupCode = response.code) }
                }
                .onFailure {
                    _uiState.update { it.copy(groupActionLoading = false, groupActionError = "Errore nella creazione del gruppo") }
                }
        }
    }

    fun joinGroup(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(groupActionLoading = true, groupActionError = null) }
            runCatching { api.joinGroup(GroupResponse(code.uppercase())) }
                .onSuccess { response ->
                    prefs.groupCode = response.code
                    _uiState.update { it.copy(groupActionLoading = false, groupCode = response.code) }
                }
                .onFailure {
                    _uiState.update { it.copy(groupActionLoading = false, groupActionError = "Codice non valido") }
                }
        }
    }

    fun leaveGroup() {
        prefs.groupCode = null
        _uiState.update { it.copy(groupCode = "") }
    }

    fun clearBackendError() {
        _uiState.update { it.copy(backendError = null) }
    }
}
