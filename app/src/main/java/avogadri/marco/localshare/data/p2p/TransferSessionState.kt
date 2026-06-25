package avogadri.marco.localshare.data.p2p

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Flag condiviso per evitare che il watcher di connessioni in arrivo avvii
 * una sessione di ricezione sul device che ha invece avviato un invio.
 */
object TransferSessionState {
    val isTransferring = AtomicBoolean(false)
}
