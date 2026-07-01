override suspend fun recordTransfer(...): String {
    // 1. persistenza locale sempre garantita
    val transferId = local.recordTransfer(peerDeviceId, fileName, sizeBytes, direction, ...)

    if (prefs.syncEnabled && prefs.jwtToken != null) {
        runCatching {
            api.recordTransfer(RecordTransferRequest(transferId = transferId, ...,
                                                     groupCode = prefs.groupCode))
            local.markSynced(transferId)
        }.onFailure {
            enqueueSyncWorker() // WorkManager ritenta quando torna la rete
        }
    }
    return transferId
}
