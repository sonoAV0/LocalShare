package avogadri.marco.localshare.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    /**
     * Inserisce una nuova entry nella tabella history
     *
     * @param entry Entità inserita
     */
    @Insert
    suspend fun insert(entry: HistoryEntity)

    /**
     * Prende tutte le entry della tabella history ordinate dalla più recente
     */
    @Query("SELECT * FROM history ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<HistoryEntity>>
}
