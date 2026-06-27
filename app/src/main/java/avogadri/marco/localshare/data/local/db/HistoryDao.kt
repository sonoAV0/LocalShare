package avogadri.marco.localshare.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(entry: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE isSynced = 0")
    suspend fun getUnsynced(): List<HistoryEntity>

    @Query("UPDATE history SET isSynced = 1 WHERE transferId = :transferId")
    suspend fun markSynced(transferId: String)

    @Query("SELECT * FROM history")
    suspend fun getAll(): List<HistoryEntity>
}
