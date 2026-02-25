package com.voices.core.network.api

import com.voices.core.common.model.Attachment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiMessage(
    val role: String,
    val content: String,
    val attachments: List<Attachment> = emptyList(),
)

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ApiMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    @SerialName("top_p") val topP: Double = 1.0,
    val stream: Boolean = true,
)

@Serializable
data class ModelResponse(
    val id: String,
    val name: String,
    val provider: String,
    val contextWindow: Int,
    val pricingPromptPerM: Double = 0.0,
    val pricingCompletionPerM: Double = 0.0,
    val supportsStreaming: Boolean,
    val supportsVision: Boolean,
)

@Serializable
data class UsageResponse(
    val monthlyTokensUsed: Long,
    val monthlyTokenLimit: Long,
)

@Serializable
data class UserProfileResponse(
    val uid: String,
    val email: String,
    val displayName: String,
    val provider: String,
    val preferredModel: String,
    val usageTier: String,
)
