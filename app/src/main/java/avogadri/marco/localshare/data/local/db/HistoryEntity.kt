package avogadri.marco.localshare.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransferDirection { SENT, RECEIVED }

/**
 * Entità che identifica una entry della tabella history
 *
 * @param id Identificatore univoco della entry
 * @param peerDeviceId Identificatore del dispositivo
 * @param fileName Nome del file trasferito
 * @param sizeBytes Dimensione del file trasferito (in byte)
 * @param direction Utilizza un enum per identificare se il file sia stato inviato o ricevuto
 * @param timestampMillis Timestamp in millisecondi della transazione
 * @param latitude Latitudine della posizione della transazione
 * @param longitude Longitude della posizione della transazione
 */
@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val peerDeviceId: String,
    val fileName: String,
    val sizeBytes: Long,
    val direction: TransferDirection,
    val timestampMillis: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
