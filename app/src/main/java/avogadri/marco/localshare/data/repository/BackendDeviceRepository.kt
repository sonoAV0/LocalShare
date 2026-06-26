package avogadri.marco.localshare.data.repository

import avogadri.marco.localshare.data.local.BackendPreferences
import avogadri.marco.localshare.data.remote.LocalShareApi
import avogadri.marco.localshare.data.remote.LoginRequest

class BackendDeviceRepository(
    private val api: LocalShareApi,
    private val prefs: BackendPreferences,
) : DeviceRepository {

    override suspend fun registerDevice(deviceId: String): Result<Unit> {
        if (!prefs.syncEnabled) return Result.success(Unit)
        if (prefs.jwtToken != null) return Result.success(Unit)

        return runCatching {
            val userId = prefs.backendUserId ?: run {
                val response = api.register()
                prefs.backendUserId = response.userId
                response.userId
            }
            val loginResponse = api.login(LoginRequest(userId))
            prefs.jwtToken = loginResponse.accessToken
        }
    }
}
