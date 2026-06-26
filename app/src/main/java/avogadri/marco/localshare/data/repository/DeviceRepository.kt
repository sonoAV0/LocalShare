package avogadri.marco.localshare.data.repository

interface DeviceRepository {
    suspend fun registerDevice(deviceId: String): Result<Unit>
}
