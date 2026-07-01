override suspend fun recordTransfer(peerDeviceId: String, fileName: String, sizeBytes: Long, direction: TransferDirection, latitude: Double?, longitude: Double?,): String {
        val transferId = UUID.randomUUID().toString()
        historyDao.insert(
            HistoryEntity(
                transferId = transferId,
                peerDeviceId = peerDeviceId,
                fileName = fileName,
                sizeBytes = sizeBytes,
                direction = direction,
                timestampMillis = System.currentTimeMillis(),
                latitude = latitude, longitude = longitude,
            ),
        )
        return transferId
    }