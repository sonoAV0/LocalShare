package avogadri.marco.localshare.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransferDirection { SENT, RECEIVED }

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transferId: String,
    val peerDeviceId: String,
    val fileName: String,
    val sizeBytes: Long,
    val direction: TransferDirection,
    val timestampMillis: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isSynced: Boolean = false,
)
