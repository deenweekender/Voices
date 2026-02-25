package com.voices.core.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface VoicesApi {
    @POST("chat/completions")
    suspend fun chatCompletions(@Body request: ChatCompletionRequest): String

    @GET("models")
    suspend fun getModels(): List<ModelResponse>

    @GET("user/profile")
    suspend fun getUserProfile(): UserProfileResponse

    @PATCH("user/settings")
    suspend fun updateSettings(@Body settings: Map<String, String>): UserProfileResponse

    @GET("user/usage")
    suspend fun getUsage(): UsageResponse

    @POST("conversations/sync")
    suspend fun syncUpload(@Body payload: Map<String, String>): Map<String, String>

    @GET("conversations/sync")
    suspend fun syncDownload(): Map<String, String>

    @DELETE("user/account")
    suspend fun deleteUserAccount(): Map<String, String>
}
