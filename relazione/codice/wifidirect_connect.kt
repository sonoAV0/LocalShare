override suspend fun connect(peer: PeerDevice) = suspendCancellableCoroutine<Unit> { cont ->
    val config = WifiP2pConfig().apply { deviceAddress = peer.address }
    manager.connect(p2pChannel, config, object : WifiP2pManager.ActionListener {
        override fun onSuccess() { cont.resume(Unit) }
        override fun onFailure(reason: Int) {
            cont.cancel(IllegalStateException("connect failed: $reason"))
        }
    })
}
