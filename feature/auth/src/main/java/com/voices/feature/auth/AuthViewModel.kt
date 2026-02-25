package com.voices.feature.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voices.core.common.util.SessionStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class AuthUiState(
    val isLoading: Boolean = false,
    val provider: String? = null,
    val message: String = "",
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onGoogleSignIn(activity: Activity? = null) {
        if (activity == null) {
            SessionStore.signIn(userId = "google-${UUID.randomUUID()}", provider = "Google")
            _uiState.value = AuthUiState(provider = "Google", message = "Google sign-in connected (mock)")
            return
        }

        signInWithOAuthProvider(activity = activity, providerId = "google.com", providerLabel = "Google")
    }

    fun onAppleSignIn(activity: Activity? = null) {
        if (activity == null) {
            SessionStore.signIn(userId = "apple-${UUID.randomUUID()}", provider = "Apple")
            _uiState.value = AuthUiState(provider = "Apple", message = "Apple sign-in connected (mock)")
            return
        }

        signInWithOAuthProvider(activity = activity, providerId = "apple.com", providerLabel = "Apple")
    }

    private fun signInWithOAuthProvider(activity: Activity, providerId: String, providerLabel: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "")

            val auth = FirebaseAuth.getInstance()
            val providerBuilder = OAuthProvider.newBuilder(providerId).apply {
                addCustomParameter("prompt", "select_account")
            }

            try {
                val authResult = auth.pendingAuthResult?.await()
                    ?: auth.startActivityForSignInWithProvider(activity, providerBuilder.build()).await()
                val user = authResult.user
                if (user != null) {
                    SessionStore.signIn(user.uid, providerLabel)
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        provider = providerLabel,
                        message = "$providerLabel sign-in successful",
                    )
                } else {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        provider = null,
                        message = "$providerLabel sign-in did not return a user",
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    provider = null,
                    message = "$providerLabel sign-in failed: ${exception.message ?: "unknown error"}",
                )
            }
        }
    }
}
