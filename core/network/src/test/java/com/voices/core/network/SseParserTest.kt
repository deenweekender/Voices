package com.voices.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SseParserTest {

    @Test
    fun `extractDeltaContent returns content for valid SSE delta`() {
        val line = "data: {\"choices\":[{\"delta\":{\"content\":\"Hello\"}}]}"

        val result = SseParser.extractDeltaContent(line)

        assertEquals("Hello", result)
    }

    @Test
    fun `extractDeltaContent returns null for done marker`() {
        val result = SseParser.extractDeltaContent("data: [DONE]")

        assertNull(result)
    }

    @Test
    fun `extractDeltaContent returns null for non data line`() {
        val result = SseParser.extractDeltaContent("event: ping")

        assertNull(result)
    }
}
