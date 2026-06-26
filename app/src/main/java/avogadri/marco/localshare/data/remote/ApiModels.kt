package avogadri.marco.localshare.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("user_id") val userId: String,
)

@Serializable
data class LoginRequest(
    @SerialName("user_id") val userId: String,
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
)

@Serializable
data class RecordTransferRequest(
    @SerialName("transfer_id") val transferId: String,
    @SerialName("peer_device_id") val peerDeviceId: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("size_bytes") val sizeBytes: Long,
    val direction: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("group_code") val groupCode: String? = null,
)

@Serializable
data class BackendTransferResponse(
    @SerialName("transfer_id") val transferId: String,
    @SerialName("peer_device_id") val peerDeviceId: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("size_bytes") val sizeBytes: Long,
    val direction: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: String,
)

@Serializable
data class GroupResponse(
    val code: String,
)
