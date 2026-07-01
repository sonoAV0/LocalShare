override suspend fun sendFile(connectionInfo: P2pConnectionInfo, fileUri: Uri): TransferResult =
        withContext(Dispatchers.IO) {
            openTransferSocket(connectionInfo).use { socket ->
                val fileName = queryFileName(fileUri) ?: "file"
                
                val sizeBytes = context.contentResolver.openAssetFileDescriptor(fileUri, "r")?.use { it.length } ?: -1L

                val output = DataOutputStream(socket.outputStream)
                output.writeUTF(fileName)
                output.writeLong(sizeBytes)
                context.contentResolver.openInputStream(fileUri)?.use { input -> input.copyTo(output) } // copio da uno stream all'altro a blocchi
                output.flush() // mando i blocchi allo stream di output

                TransferResult(fileName, sizeBytes)
            }
        }