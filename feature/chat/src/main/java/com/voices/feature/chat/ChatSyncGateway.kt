package com.voices.feature.chat

import com.voices.core.database.ChatStore
import com.voices.core.network.api.VoicesApi

interface ChatSyncGateway {
    suspend fun sync(userId: String): String
}

class NetworkChatSyncGateway(
    private val api: VoicesApi,
    private val store: ChatStore,
) : ChatSyncGateway {
    override suspend fun sync(userId: String): String {
        val payload = store.buildSyncPayload(userId)
        val uploadResponse = api.syncUpload(payload)
        val downloadResponse = api.syncDownload()

        val uploaded = payload["conversationCount"] ?: "0"
        val downloaded = downloadResponse["conversationCount"] ?: downloadResponse.size.toString()
        val uploadStatus = uploadResponse["status"] ?: "ok"

        return "Sync $uploadStatus: uploaded=$uploaded downloaded=$downloaded"
    }
}
