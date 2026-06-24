package avogadri.marco.localshare.data.repository

import avogadri.marco.localshare.data.local.db.HistoryDao
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.local.db.TransferDirection
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeHistory(): Flow<List<HistoryEntity>>
    suspend fun recordTransfer(
        peerDeviceId: String,
        fileName: String,
        sizeBytes: Long,
        direction: TransferDirection,
        latitude: Double? = null,
        longitude: Double? = null,
    )
}

/**
 * Local-only for now: every transfer is persisted on-device via Room.
 * A future revision will also push entries to the Flask backend from here,
 * keeping ViewModels unaware of where the data actually lives.
 */
class LocalHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {
    override fun observeHistory(): Flow<List<HistoryEntity>> = historyDao.observeAll()

    override suspend fun recordTransfer(
        peerDeviceId: String,
        fileName: String,
        sizeBytes: Long,
        direction: TransferDirection,
        latitude: Double?,
        longitude: Double?,
    ) {
        historyDao.insert(
            HistoryEntity(
                peerDeviceId = peerDeviceId,
                fileName = fileName,
                sizeBytes = sizeBytes,
                direction = direction,
                timestampMillis = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
            ),
        )
    }
}
