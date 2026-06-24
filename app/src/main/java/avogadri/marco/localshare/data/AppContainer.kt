package avogadri.marco.localshare.data

import android.content.Context
import avogadri.marco.localshare.data.local.DeviceIdProvider
import avogadri.marco.localshare.data.local.db.LocalShareDatabase
import avogadri.marco.localshare.data.repository.DeviceRepository
import avogadri.marco.localshare.data.repository.FakeDeviceRepository
import avogadri.marco.localshare.data.p2p.P2pManager
import avogadri.marco.localshare.data.p2p.WifiDirectP2pManager
import avogadri.marco.localshare.data.repository.HistoryRepository
import avogadri.marco.localshare.data.repository.LocalHistoryRepository

/**
 * Singleton contenente tutte le dipendenze dell'applicazione
 */
object AppContainer {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val deviceIdProvider: DeviceIdProvider by lazy { DeviceIdProvider(appContext) }
    val deviceRepository: DeviceRepository by lazy { FakeDeviceRepository() }

    private val database: LocalShareDatabase by lazy { LocalShareDatabase.build(appContext) }
    val historyRepository: HistoryRepository by lazy { LocalHistoryRepository(database.historyDao()) }

    val p2pManager: P2pManager by lazy { WifiDirectP2pManager(appContext) }
}
