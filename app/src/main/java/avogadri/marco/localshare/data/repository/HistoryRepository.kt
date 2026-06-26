package avogadri.marco.localshare.data.repository

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
    ): String

    suspend fun fetchGroupHistory(): List<HistoryEntity> = emptyList()
}
