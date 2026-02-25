package com.voices.core.database

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversations",
    indices = [Index("userId"), Index("updatedAt")],
)
data class ConversationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val defaultModelId: String,
    val systemPrompt: String? = null,
    val totalTokens: Int = 0,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "messages",
    indices = [Index("conversationId"), Index("createdAt"), Index("status")],
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val modelId: String? = null,
    val parentMessageId: String? = null,
    val tokenCount: Int = 0,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Fts4(contentEntity = MessageEntity::class)
@Entity(tableName = "messages_fts")
data class MessageFtsEntity(
    @PrimaryKey val rowid: Long,
    val content: String,
)
