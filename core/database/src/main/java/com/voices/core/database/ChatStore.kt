package com.voices.core.database

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChatStore private constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
) {
    fun observeConversations(userId: String): Flow<List<ConversationEntity>> =
        conversationDao.observeConversations(userId)

    suspend fun getOrCreateActiveConversation(userId: String, defaultModelId: String): String {
        val latest = conversationDao.latestConversation(userId)
        if (latest != null) return latest.id

        val now = System.currentTimeMillis()
        val conversation = ConversationEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = "New Chat",
            defaultModelId = defaultModelId,
            createdAt = now,
            updatedAt = now,
        )
        conversationDao.upsert(conversation)
        return conversation.id
    }

    fun observeMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.observeMessages(conversationId)

    suspend fun appendUserMessage(conversationId: String, content: String) {
        val now = System.currentTimeMillis()
        messageDao.upsert(
            MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "USER",
                content = content,
                status = "COMPLETE",
                createdAt = now,
                updatedAt = now,
            ),
        )
        updateConversationTitleAndTime(conversationId, content, now)
    }

    suspend fun appendAssistantMessage(conversationId: String, content: String) {
        val now = System.currentTimeMillis()
        messageDao.upsert(
            MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "ASSISTANT",
                content = content,
                status = "COMPLETE",
                createdAt = now,
                updatedAt = now,
            ),
        )
        touchConversation(conversationId, now)
    }

    fun observeHistoryPreview(userId: String): Flow<List<String>> {
        return observeConversations(userId).map { items ->
            items.map { entity ->
                "${entity.title} • ${entity.defaultModelId}"
            }
        }
    }

    suspend fun buildSyncPayload(userId: String): Map<String, String> {
        val conversations = observeConversations(userId).first()
        return mapOf(
            "userId" to userId,
            "conversationCount" to conversations.size.toString(),
            "lastSyncedAt" to System.currentTimeMillis().toString(),
        )
    }

    private suspend fun updateConversationTitleAndTime(conversationId: String, firstPrompt: String, now: Long) {
        val conversation = conversationDao.getById(conversationId) ?: return
        val title = if (conversation.title == "New Chat") {
            firstPrompt.take(40).ifBlank { "New Chat" }
        } else {
            conversation.title
        }
        conversationDao.upsert(conversation.copy(title = title, updatedAt = now))
    }

    private suspend fun touchConversation(conversationId: String, now: Long) {
        val conversation = conversationDao.getById(conversationId) ?: return
        conversationDao.upsert(conversation.copy(updatedAt = now))
    }

    companion object {
        fun create(context: Context): ChatStore {
            val db = VoicesDatabase.get(context)
            return ChatStore(db.conversationDao(), db.messageDao())
        }
    }
}
