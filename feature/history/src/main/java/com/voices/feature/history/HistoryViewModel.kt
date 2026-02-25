package com.voices.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voices.core.common.util.SessionStore
import com.voices.core.database.ChatStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val items: List<String> = emptyList(),
)

class HistoryViewModel(
    private val chatStore: ChatStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatStore.observeHistoryPreview(SessionStore.userId.value).collect { rows ->
                _uiState.update { it.copy(items = rows) }
            }
        }
    }

    companion object {
        fun factory(chatStore: ChatStore): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(chatStore) as T
                }
            }
        }
    }
}
