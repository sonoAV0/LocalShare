package avogadri.marco.localshare.data.p2p

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

private const val TRANSFER_PORT = 8988
private const val SOCKET_TIMEOUT_MILLIS = 15_000

data class TransferResult(
    val fileName: String,
    val sizeBytes: Long,
)

interface TransferManager {
    suspend fun sendFile(connectionInfo: P2pConnectionInfo, fileUri: Uri): TransferResult
    suspend fun receiveFile(connectionInfo: P2pConnectionInfo): TransferResult
}

/**
 * Classe che gestisce la comunicazione tra peer tramite socket TCP
 */
class SocketTransferManager(private val context: Context) : TransferManager {

    override suspend fun sendFile(connectionInfo: P2pConnectionInfo, fileUri: Uri): TransferResult =
        withContext(Dispatchers.IO) {
            openTransferSocket(connectionInfo).use { socket ->
                val fileName = queryFileName(fileUri) ?: "file"
                // ottiene la dimensione del file senza leggerlo
                val sizeBytes = context.contentResolver.openAssetFileDescriptor(fileUri, "r")?.use { it.length } ?: -1L

                val output = DataOutputStream(socket.outputStream)
                output.writeUTF(fileName)
                output.writeLong(sizeBytes)
                context.contentResolver.openInputStream(fileUri)?.use { input -> input.copyTo(output) } // copio da uno stream all'altro a blocchi
                output.flush() // mando i blocchi allo stream di output

                TransferResult(fileName, sizeBytes)
            }
        }

    override suspend fun receiveFile(connectionInfo: P2pConnectionInfo): TransferResult =
        withContext(Dispatchers.IO) {
            openTransferSocket(connectionInfo).use { socket ->
                val input = DataInputStream(socket.inputStream)
                val fileName = input.readUTF()
                val sizeBytes = input.readLong()

                val outFile = File(context.getExternalFilesDir(null), fileName)
                outFile.outputStream().use { output -> input.copyTo(output) }

                TransferResult(fileName, sizeBytes)
            }
        }

    // apre il socket gestendo il caso in cui il peer sia il server o il client
    private fun openTransferSocket(connectionInfo: P2pConnectionInfo): Socket =
        if (connectionInfo.isGroupOwner) {
            ServerSocket(TRANSFER_PORT).use { it.accept() }
        } else {
            Socket().apply {
                // lega il socket alla rete Wi-Fi Direct, altrimenti il sistema instrada la
                // connessione sulla rete di default (es. dati mobili) e il connect va in timeout
                findP2pNetwork()?.bindSocket(this)
                connect(InetSocketAddress(connectionInfo.groupOwnerAddress, TRANSFER_PORT), SOCKET_TIMEOUT_MILLIS)
            }
        }

    // individua, tra tutte le rete attive, quella corrispondente all'interfaccia Wi-Fi Direct
    private fun findP2pNetwork(): Network? {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        return connectivityManager.allNetworks.firstOrNull { network ->
            connectivityManager.getLinkProperties(network)?.interfaceName?.contains("p2p") == true
        }
    }

    // Metodo che interroga il contentResolver per ottenere il display_name del file
    private fun queryFileName(uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) return cursor.getString(nameIndex)
        }
        return null
    }
}
