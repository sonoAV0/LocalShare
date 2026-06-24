package avogadri.marco.localshare.data.repository

import kotlinx.coroutines.delay

interface DeviceRepository {
    suspend fun registerDevice(deviceId: String): Result<Unit>
}

/**
 * TODO: Implementare backend in Flask
 */
class FakeDeviceRepository : DeviceRepository {
    override suspend fun registerDevice(deviceId: String): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
