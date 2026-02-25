package com.voices.feature.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage appends user and assistant messages`() = runTest {
        val viewModel = ChatViewModel()
        viewModel.onInputChanged("Hello")

        viewModel.sendMessage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.input)
        assertFalse(state.isStreaming)
        assertEquals(2, state.messages.size)
        assertTrue(state.messages[0].startsWith("You: Hello"))
        assertTrue(state.messages[1].startsWith("Assistant: "))
    }

    @Test
    fun `stopStreaming clears streaming flag`() = runTest {
        val viewModel = ChatViewModel()
        viewModel.onInputChanged("Test")
        viewModel.sendMessage()

        viewModel.stopStreaming()

        assertFalse(viewModel.uiState.value.isStreaming)
    }
}
