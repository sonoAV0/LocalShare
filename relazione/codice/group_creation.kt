val connectionInfo = withTimeoutOrNull(30_000) {
    AppContainer.p2pManager.observeConnectionInfo().filterNotNull().first() 
}