package com.voices.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY isPinned DESC, updatedAt DESC")
    fun observeConversations(userId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun latestConversation(userId: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(conversation: ConversationEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(message: MessageEntity)

    @Query("UPDATE messages SET content = :content, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMessageContent(id: String, content: String, status: String, updatedAt: Long)
}
