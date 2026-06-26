package avogadri.marco.localshare.data.repository

import avogadri.marco.localshare.data.local.db.HistoryDao
import avogadri.marco.localshare.data.local.db.HistoryEntity
import avogadri.marco.localshare.data.local.db.TransferDirection
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LocalHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {

    override fun observeHistory(): Flow<List<HistoryEntity>> = historyDao.observeAll()

    override suspend fun recordTransfer(
        peerDeviceId: String,
        fileName: String,
        sizeBytes: Long,
        direction: TransferDirection,
        latitude: Double?,
        longitude: Double?,
    ): String {
        val transferId = UUID.randomUUID().toString()
        historyDao.insert(
            HistoryEntity(
                transferId = transferId,
                peerDeviceId = peerDeviceId,
                fileName = fileName,
                sizeBytes = sizeBytes,
                direction = direction,
                timestampMillis = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
            ),
        )
        return transferId
    }

    suspend fun getUnsynced(): List<HistoryEntity> = historyDao.getUnsynced()

    suspend fun markSynced(transferId: String) = historyDao.markSynced(transferId)
}
