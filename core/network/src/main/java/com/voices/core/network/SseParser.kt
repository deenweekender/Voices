package com.voices.core.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SseParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun extractDeltaContent(line: String): String? {
        if (!line.startsWith("data:")) return null
        val payload = line.removePrefix("data:").trim()
        if (payload == "[DONE]") return null

        return runCatching {
            val parsed = json.parseToJsonElement(payload)
            parsed.jsonObject["choices"]
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("delta")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.content
        }.getOrNull()
    }
}
