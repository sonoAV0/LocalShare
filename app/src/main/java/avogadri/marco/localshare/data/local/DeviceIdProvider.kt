package avogadri.marco.localshare.data.local

import android.content.Context
import java.util.UUID

/**
 * Classe che genera o recupera se già esistente un id univoco per dispositivo (UUID)
 */
class DeviceIdProvider(context: Context) {

    // storage interno persistente usato per leggere/scrivere lo UUID
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getOrCreateDeviceId(): String {
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) return existing

        val generated = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, generated).apply()
        return generated
    }

    companion object {
        private const val PREFS_NAME = "device_identity"
        private const val KEY_DEVICE_ID = "device_id"
    }
}
