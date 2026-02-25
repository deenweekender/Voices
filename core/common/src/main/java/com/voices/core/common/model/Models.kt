package com.voices.core.common.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ModelInfo(
    val id: String,
    val displayName: String,
    val provider: String,
    val contextWindow: Int,
    val supportsStreaming: Boolean,
    val supportsVision: Boolean,
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
}

enum class MessageStatus {
    SENDING,
    STREAMING,
    COMPLETE,
    ERROR,
}

@Serializable
data class Attachment(
    val type: String,
    val url: String,
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val modelId: String? = null,
    val parentMessageId: String? = null,
    val tokenCount: Int = 0,
    val attachments: List<Attachment> = emptyList(),
    val status: MessageStatus = MessageStatus.COMPLETE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt,
)

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val defaultModelId: String,
    val systemPrompt: String? = null,
    val totalTokens: Int = 0,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt,
)

object SupportedModels {
    val all = listOf(
        ModelInfo("gpt-4o", "GPT-4o", "OpenAI", 128000, true, true),
        ModelInfo("gpt-4o-mini", "GPT-4o Mini", "OpenAI", 128000, true, true),
        ModelInfo("o3-mini", "o3-mini (Reasoning)", "OpenAI", 200000, true, false),
        ModelInfo("claude-sonnet-4-20250514", "Claude Sonnet 4", "Anthropic", 200000, true, true),
        ModelInfo("claude-haiku-4-5-20251001", "Claude Haiku 4.5", "Anthropic", 200000, true, true),
        ModelInfo("gemini-2.0-flash", "Gemini 2.0 Flash", "Google", 1_000_000, true, true),
        ModelInfo("gemini-2.5-pro", "Gemini 2.5 Pro", "Google", 1_000_000, true, true),
        ModelInfo("llama-3.3-70b", "Llama 3.3 70B", "Meta", 128000, false, false),
        ModelInfo("mistral-large", "Mistral Large", "Mistral", 128000, true, false),
        ModelInfo("deepseek-r1", "DeepSeek R1", "DeepSeek", 64000, true, false),
    )
}
