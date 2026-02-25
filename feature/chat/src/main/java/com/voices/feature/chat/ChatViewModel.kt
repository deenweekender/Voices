package com.voices.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voices.core.common.model.SupportedModels
import com.voices.core.common.util.AppConstants
import com.voices.core.common.util.SessionStore
import com.voices.core.database.ChatStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatUiState(
    val modelId: String = AppConstants.DEFAULT_MODEL,
    val input: String = "",
    val messages: List<String> = emptyList(),
    val isStreaming: Boolean = false,
    val syncStatus: String = "",
)

class ChatViewModel(
    private val store: ChatStore? = null,
    private val syncGateway: ChatSyncGateway? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val models = SupportedModels.all

    private var streamJob: Job? = null
    private var activeConversationId: String? = null

    init {
        if (store != null) {
            viewModelScope.launch {
                val userId = SessionStore.userId.value
                val defaultModelId = SessionStore.defaultModelId.value
                activeConversationId = store.getOrCreateActiveConversation(userId, defaultModelId)
                store.observeMessages(activeConversationId.orEmpty()).collect { entities ->
                    _uiState.update {
                        it.copy(
                            modelId = defaultModelId,
                            messages = entities.map { entity ->
                                if (entity.role == "USER") "You: ${entity.content}" else "Assistant: ${entity.content}"
                            },
                        )
                    }
                }
            }
        }
    }

    fun onInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
    }

    fun selectModel(modelId: String) {
        SessionStore.setDefaultModel(modelId)
        _uiState.value = _uiState.value.copy(modelId = modelId)
    }

    fun sendMessage() {
        val prompt = _uiState.value.input.trim()
        if (prompt.isEmpty()) return

        if (store != null) {
            sendMessagePersisted(prompt)
            return
        }

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + "You: $prompt",
            input = "",
            isStreaming = true,
        )

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            val chunks = listOf("Thinking", "…", "done. Echo: ", prompt)
            var acc = "Assistant: "
            for (chunk in chunks) {
                delay(250)
                acc += chunk
            }
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + acc,
                isStreaming = false,
            )
        }
    }

    fun stopStreaming() {
        streamJob?.cancel()
        _uiState.value = _uiState.value.copy(isStreaming = false)
    }

    fun syncNow() {
        val chatStore = store ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatus = "Syncing…") }
            val userId = SessionStore.userId.value
            val status = if (syncGateway != null) {
                runCatching { syncGateway.sync(userId) }
                    .getOrElse { "Sync failed: ${it.message ?: "unknown error"}" }
            } else {
                val payload = chatStore.buildSyncPayload(userId)
                "Sync payload ready: conversations=${payload["conversationCount"]}"
            }
            _uiState.update {
                it.copy(syncStatus = status)
            }
        }
    }

    private fun sendMessagePersisted(prompt: String) {
        val chatStore = store ?: return
        val conversationId = activeConversationId ?: return

        _uiState.update { it.copy(input = "", isStreaming = true) }

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            chatStore.appendUserMessage(conversationId, prompt)
            val chunks = listOf("Thinking", "…", "done. Echo: ", prompt)
            var acc = ""
            for (chunk in chunks) {
                delay(250)
                acc += chunk
            }
            chatStore.appendAssistantMessage(conversationId, acc)
            _uiState.update { it.copy(isStreaming = false) }
        }
    }

    companion object {
        fun factory(store: ChatStore, syncGateway: ChatSyncGateway? = null): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(store, syncGateway) as T
                }
            }
        }
    }
}
