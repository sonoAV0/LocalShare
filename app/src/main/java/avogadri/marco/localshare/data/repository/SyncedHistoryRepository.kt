package avogadri.marco.localshare.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import avogadri.marco.localshare.data.local.BackendPreferences
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.local.db.TransferDirection
import avogadri.marco.localshare.data.remote.BackendTransferResponse
import avogadri.marco.localshare.data.remote.LocalShareApi
import avogadri.marco.localshare.data.remote.RecordTransferRequest
import avogadri.marco.localshare.service.SyncWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.Instant

class SyncedHistoryRepository(
    private val local: LocalHistoryRepository,
    private val api: LocalShareApi,
    private val prefs: BackendPreferences,
    private val workManager: WorkManager,
) : HistoryRepository {

    override fun observeHistory(): Flow<List<HistoryEntity>> = local.observeHistory()

    override suspend fun recordTransfer(
        peerDeviceId: String,
        fileName: String,
        sizeBytes: Long,
        direction: TransferDirection,
        latitude: Double?,
        longitude: Double?,
    ): String {
        val transferId = local.recordTransfer(peerDeviceId, fileName, sizeBytes, direction, latitude, longitude)

        if (prefs.syncEnabled && prefs.jwtToken != null) {
            runCatching {
                api.recordTransfer(
                    RecordTransferRequest(
                        transferId = transferId,
                        peerDeviceId = peerDeviceId,
                        fileName = fileName,
                        sizeBytes = sizeBytes,
                        direction = direction.name,
                        latitude = latitude,
                        longitude = longitude,
                        groupCode = prefs.groupCode,
                    )
                )
                local.markSynced(transferId)
            }.onFailure {
                enqueueSyncWorker()
            }
        }

        return transferId
    }

    override fun observeMergedHistory(): Flow<List<HistoryEntity>> {
        val remoteFlow = flow {
            while (true) {
                emit(fetchGroupHistory())
                delay(30_000)
            }
        }
        return combine(local.observeHistory(), remoteFlow) { local, remote ->
            val localIds = local.map { it.transferId }.toSet()
            val remoteOnly = remote.filter { it.transferId !in localIds }
            (local + remoteOnly)
                .distinctBy { it.transferId }
                .sortedByDescending { it.timestampMillis }
        }
    }

    override suspend fun associateToGroup(groupCode: String) {
        local.getAll().forEach { entry ->
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
                        groupCode = groupCode,
                    )
                )
            }
        }
    }

    override suspend fun fetchGroupHistory(): List<HistoryEntity> {
        val code = prefs.groupCode ?: return emptyList()
        if (!prefs.syncEnabled || prefs.jwtToken == null) return emptyList()

        return runCatching {
            api.getGroupHistory(code).map { it.toHistoryEntity() }
        }.getOrDefault(emptyList())
    }

    private fun enqueueSyncWorker() {
        workManager.enqueueUniqueWork(
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

    private fun BackendTransferResponse.toHistoryEntity() = HistoryEntity(
        transferId = transferId,
        peerDeviceId = peerDeviceId,
        fileName = fileName,
        sizeBytes = sizeBytes,
        direction = TransferDirection.valueOf(direction),
        timestampMillis = Instant.parse(timestamp).toEpochMilli(),
        latitude = latitude,
        longitude = longitude,
        isSynced = true,
    )
}
