package avogadri.marco.localshare.data.repository

import kotlinx.coroutines.delay

class FakeDeviceRepository : DeviceRepository {
    override suspend fun registerDevice(deviceId: String): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
