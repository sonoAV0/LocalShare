private fun openTransferSocket(connectionInfo: P2pConnectionInfo): Socket =
    if (connectionInfo.isGroupOwner) {
        ServerSocket().apply {
            reuseAddress = true
            soTimeout = SOCKET_TIMEOUT_MILLIS
            bind(InetSocketAddress(TRANSFER_PORT))
        }.use { it.accept() }
    } else {
        Socket().apply {
            findP2pNetwork()?.bindSocket(this)
            connect(InetSocketAddress(connectionInfo.groupOwnerAddress, TRANSFER_PORT),
                    SOCKET_TIMEOUT_MILLIS)
        }
    }
