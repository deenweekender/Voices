package com.voices.feature.profile

import androidx.lifecycle.ViewModel
import com.voices.core.common.util.SessionStore
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiState(
    val userId: String,
    val provider: String,
)

class ProfileViewModel : ViewModel() {
    val userId: StateFlow<String> = SessionStore.userId
    val provider: StateFlow<String> = SessionStore.provider
}
