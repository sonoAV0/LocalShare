package avogadri.marco.localshare.data.local

import android.content.Context

class BackendPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var syncEnabled: Boolean
        get() = prefs.getBoolean(KEY_SYNC_ENABLED, false)
        set(value) { prefs.edit().putBoolean(KEY_SYNC_ENABLED, value).apply() }

    var jwtToken: String?
        get() = prefs.getString(KEY_JWT_TOKEN, null)
        set(value) { prefs.edit().putString(KEY_JWT_TOKEN, value).apply() }

    var backendUserId: String?
        get() = prefs.getString(KEY_BACKEND_USER_ID, null)
        set(value) { prefs.edit().putString(KEY_BACKEND_USER_ID, value).apply() }

    var groupCode: String?
        get() = prefs.getString(KEY_GROUP_CODE, null)
        set(value) { prefs.edit().putString(KEY_GROUP_CODE, value).apply() }

    companion object {
        private const val PREFS_NAME = "backend_prefs"
        private const val KEY_SYNC_ENABLED = "sync_enabled"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_BACKEND_USER_ID = "backend_user_id"
        private const val KEY_GROUP_CODE = "group_code"
    }
}
