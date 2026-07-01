package avogadri.marco.localshare.data

import android.content.Context
import androidx.work.WorkManager
import avogadri.marco.localshare.data.local.BackendPreferences
import avogadri.marco.localshare.data.local.DeviceIdProvider
import avogadri.marco.localshare.data.local.db.LocalShareDatabase
import avogadri.marco.localshare.data.p2p.IncomingTransferWatcher
import avogadri.marco.localshare.data.p2p.P2pManager
import avogadri.marco.localshare.data.p2p.SocketTransferManager
import avogadri.marco.localshare.data.p2p.TransferManager
import avogadri.marco.localshare.data.p2p.WifiDirectP2pManager
import avogadri.marco.localshare.data.remote.AuthInterceptor
import avogadri.marco.localshare.data.remote.LocalShareApi
import avogadri.marco.localshare.data.repository.BackendDeviceRepository
import avogadri.marco.localshare.data.repository.DeviceRepository
import avogadri.marco.localshare.data.repository.HistoryRepository
import avogadri.marco.localshare.data.repository.LocalHistoryRepository
import avogadri.marco.localshare.data.repository.SyncedHistoryRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object AppContainer {

    private const val BACKEND_BASE_URL = "http://10.0.2.2:5000/"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val deviceIdProvider: DeviceIdProvider by lazy { DeviceIdProvider(appContext) }
    val backendPreferences: BackendPreferences by lazy { BackendPreferences(appContext) }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(backendPreferences))
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val localShareApi: LocalShareApi by lazy { retrofit.create(LocalShareApi::class.java) }

    val deviceRepository: DeviceRepository by lazy {
        BackendDeviceRepository(localShareApi, backendPreferences)
    }

    private val database: LocalShareDatabase by lazy { LocalShareDatabase.build(appContext) }

    val localHistoryRepository: LocalHistoryRepository by lazy {
        LocalHistoryRepository(database.historyDao())
    }

    private val workManager: WorkManager by lazy { WorkManager.getInstance(appContext) }

    val historyRepository: HistoryRepository by lazy {
        SyncedHistoryRepository(localHistoryRepository, localShareApi, backendPreferences, workManager)
    }

    val p2pManager: P2pManager by lazy { WifiDirectP2pManager(appContext) }
    val transferManager: TransferManager by lazy { SocketTransferManager(appContext) }
    val incomingTransferWatcher: IncomingTransferWatcher by lazy {
        IncomingTransferWatcher(appContext, p2pManager)
    }
}
