package com.voices.feature.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun `onGoogleSignIn updates provider and message`() {
        val viewModel = AuthViewModel()

        viewModel.onGoogleSignIn()

        val state = viewModel.uiState.value
        assertEquals("Google", state.provider)
        assertEquals("Google sign-in connected (mock)", state.message)
    }

    @Test
    fun `onAppleSignIn updates provider and message`() {
        val viewModel = AuthViewModel()

        viewModel.onAppleSignIn()

        val state = viewModel.uiState.value
        assertEquals("Apple", state.provider)
        assertEquals("Apple sign-in connected (mock)", state.message)
    }
}
