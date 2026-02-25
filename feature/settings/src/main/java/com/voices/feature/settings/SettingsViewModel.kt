package com.voices.feature.settings

import androidx.lifecycle.ViewModel
import com.voices.core.common.model.SupportedModels
import com.voices.core.common.util.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val defaultModelId: String = SessionStore.defaultModelId.value,
    val options: List<String> = SupportedModels.all.map { it.id },
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setDefaultModel(modelId: String) {
        SessionStore.setDefaultModel(modelId)
        _uiState.update { it.copy(defaultModelId = modelId) }
    }
}
