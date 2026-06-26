package avogadri.marco.localshare.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import avogadri.marco.localshare.data.AppContainer
import avogadri.marco.localshare.data.remote.RecordTransferRequest

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = AppContainer.backendPreferences
        if (!prefs.syncEnabled || prefs.jwtToken == null) return Result.success()

        val localRepo = AppContainer.localHistoryRepository
        val api = AppContainer.localShareApi

        val unsynced = localRepo.getUnsynced()
        var allSucceeded = true

        for (entry in unsynced) {
            runCatching {
                api.recordTransfer(
                    RecordTransferRequest(
                        transferId = entry.transferId,
                        peerDeviceId = entry.peerDeviceId,
                        fileName = entry.fileName,
                        sizeBytes = entry.sizeBytes,
                        direction = entry.direction.name,
                        latitude = entry.latitude,
                        longitude = entry.longitude,
                        groupCode = prefs.groupCode,
                    )
                )
                localRepo.markSynced(entry.transferId)
            }.onFailure {
                allSucceeded = false
            }
        }

        return if (allSucceeded) Result.success() else Result.retry()
    }

    companion object {
        const val WORK_NAME = "history_sync"
    }
}
