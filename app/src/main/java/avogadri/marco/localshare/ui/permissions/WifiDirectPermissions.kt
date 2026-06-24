package avogadri.marco.localshare.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

val WIFI_DIRECT_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.NEARBY_WIFI_DEVICES,
)

private fun hasWifiDirectPermissions(context: Context): Boolean =
    WIFI_DIRECT_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

/**
 * Funzione che controlla se i permessi sono già stati concessi. Se sì, invoca [onGranted]
 * altrimenti apre il dialog di sistema per richiederli.
 */
@Composable
fun rememberWifiDirectPermissionRequester(onGranted: () -> Unit): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (results.values.all { it }) onGranted()
    }

    return remember(context) {
        {
            if (hasWifiDirectPermissions(context)) {
                onGranted()
            } else {
                launcher.launch(WIFI_DIRECT_PERMISSIONS)
            }
        }
    }
}
