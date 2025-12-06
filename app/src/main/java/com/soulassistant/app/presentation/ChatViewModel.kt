package com.soulassistant.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulassistant.app.data.manager.AppManager
import com.soulassistant.app.data.manager.CommunicationManager
import com.soulassistant.app.data.manager.SystemManager
import com.soulassistant.app.data.model.ActionType
import com.soulassistant.app.data.model.AiResponse
import com.soulassistant.app.data.service.JarvisNotificationListener
import com.soulassistant.app.domain.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isAction: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val commandExecutor: com.soulassistant.app.domain.executor.JarvisCommandExecutor,
    private val ttsManager: com.soulassistant.app.data.manager.TTSManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun processQuery(query: String) {
        addMessage(ChatMessage(query, isUser = true))
        _isLoading.value = true

        viewModelScope.launch {
            val result = geminiRepository.generateContent(query)
            _isLoading.value = false

            result.onSuccess { response ->
                handleAiResponse(response)
            }.onFailure { error ->
                addMessage(ChatMessage("Error: ${error.message}", isUser = false))
            }
        }
    }

    private fun handleAiResponse(response: AiResponse) {
        // 1. Show spoken message
        if (!response.message.isNullOrBlank()) {
            addMessage(ChatMessage(response.message, isUser = false))
            // Speak the response
            ttsManager.speak(response.message)
        }

        // 2. Execute Action(s)
        viewModelScope.launch {
            val result = if (response.actions != null && response.actions.isNotEmpty()) {
                // Multi-step automation
                executeActionSequence(response.actions)
            } else if (response.action != null) {
                // Single action
                executeLegacyAction(response.action, response.params ?: emptyMap())
            } else {
                "No action to execute"
            }
            
            addMessage(ChatMessage(result, isUser = false, isAction = true))
            // Speak action result
            ttsManager.speak(result)
        }
    }

    private suspend fun executeActionSequence(actionSteps: List<com.soulassistant.app.data.model.ActionStep>): String {
        val commands = mutableListOf<com.soulassistant.app.domain.model.JarvisCommand>()
        
        for (step in actionSteps) {
            val command = mapActionToCommand(step.action, step.params)
            if (command != null) {
                commands.add(command)
            }
        }
        
        return if (commands.isNotEmpty()) {
            commandExecutor.execute(commands)
        } else {
            "Could not execute action sequence"
        }
    }

    private fun mapActionToCommand(actionName: String, params: Map<String, Any>): com.soulassistant.app.domain.model.JarvisCommand? {
        return when (actionName) {
            "OPEN_APP" -> {
                val appName = params["app_name"] as? String
                if (appName != null) com.soulassistant.app.domain.model.JarvisCommand.OpenApp(appName) else null
            }
            "TAP" -> {
                val text = params["text"] as? String
                if (text != null) com.soulassistant.app.domain.model.JarvisCommand.Tap(text = text) else null
            }
            "TYPE" -> {
                val text = params["text"] as? String
                if (text != null) com.soulassistant.app.domain.model.JarvisCommand.InputText(text) else null
            }
            "SCROLL_DOWN" -> com.soulassistant.app.domain.model.JarvisCommand.ScrollDown
            "SCROLL_UP" -> com.soulassistant.app.domain.model.JarvisCommand.ScrollUp
            "BACK" -> com.soulassistant.app.domain.model.JarvisCommand.Back
            "HOME" -> com.soulassistant.app.domain.model.JarvisCommand.Home
            "CALL" -> {
                val number = params["number"] as? String
                if (number != null) com.soulassistant.app.domain.model.JarvisCommand.MakeCall(number) else null
            }
            "CREATE_FOLDER" -> {
                val name = params["name"] as? String
                if (name != null) com.soulassistant.app.domain.model.JarvisCommand.CreateFolder(name) else null
            }
            "PLAY_MUSIC" -> {
                val query = params["query"] as? String
                if (query != null) com.soulassistant.app.domain.model.JarvisCommand.PlayMusic(query) else null
            }
            "SEARCH" -> {
                val query = params["query"] as? String
                if (query != null) com.soulassistant.app.domain.model.JarvisCommand.Search(query) else null
            }
            "TOGGLE_WIFI" -> {
                val enable = params["enable"] as? Boolean ?: true
                com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("wifi", enable)
            }
            "TOGGLE_BLUETOOTH" -> {
                val enable = params["enable"] as? Boolean ?: true
                com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("bluetooth", enable)
            }
            "FLASHLIGHT" -> {
                val enable = params["enable"] as? Boolean ?: true
                com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("flashlight", enable)
            }
            else -> null
        }
    }

    private suspend fun executeLegacyAction(actionName: String, params: Map<String, Any>): String {
        val commands = mutableListOf<com.soulassistant.app.domain.model.JarvisCommand>()
        
        when (actionName) {
            "OPEN_APP" -> {
                val appName = params["app_name"] as? String
                if (appName != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.OpenApp(appName))
            }
            "SEND_MESSAGE" -> {
                val recipient = params["recipient"] as? String
                val message = params["message"] as? String
                val channel = params["channel"] as? String ?: "sms"
                if (recipient != null && message != null) {
                    commands.add(com.soulassistant.app.domain.model.JarvisCommand.SendMessage(recipient, message, channel))
                }
            }
            "TOGGLE_WIFI" -> {
                val enable = params["enable"] as? Boolean ?: true
                commands.add(com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("wifi", enable))
            }
            "TOGGLE_BLUETOOTH" -> {
                val enable = params["enable"] as? Boolean ?: true
                commands.add(com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("bluetooth", enable))
            }
            "FLASHLIGHT" -> {
                val enable = params["enable"] as? Boolean ?: true
                commands.add(com.soulassistant.app.domain.model.JarvisCommand.ToggleSetting("flashlight", enable))
            }
            "READ_NOTIFICATIONS" -> {
                commands.add(com.soulassistant.app.domain.model.JarvisCommand.OpenNotifications)
            }
            // New Actions Mapping
            "SCROLL_DOWN" -> commands.add(com.soulassistant.app.domain.model.JarvisCommand.ScrollDown)
            "SCROLL_UP" -> commands.add(com.soulassistant.app.domain.model.JarvisCommand.ScrollUp)
            "TAP" -> {
                val text = params["text"] as? String
                if (text != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.Tap(text = text))
            }
            "TYPE" -> {
                val text = params["text"] as? String
                if (text != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.InputText(text))
            }
            "BACK" -> commands.add(com.soulassistant.app.domain.model.JarvisCommand.Back)
            "HOME" -> commands.add(com.soulassistant.app.domain.model.JarvisCommand.Home)
            "CALL" -> {
                val number = params["number"] as? String
                if (number != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.MakeCall(number))
            }
            "CREATE_FOLDER" -> {
                val name = params["name"] as? String
                if (name != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.CreateFolder(name))
            }
            "PLAY_MUSIC" -> {
                val query = params["query"] as? String
                if (query != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.PlayMusic(query))
            }
            "SEARCH" -> {
                val query = params["query"] as? String
                if (query != null) commands.add(com.soulassistant.app.domain.model.JarvisCommand.Search(query))
            }
        }

        return if (commands.isNotEmpty()) {
            commandExecutor.execute(commands)
        } else {
            if (actionName == "GENERAL_ANSWER") "Done" else "Unknown or invalid action: $actionName"
        }
    }

    private fun addMessage(msg: ChatMessage) {
        val current = _messages.value.toMutableList()
        current.add(msg)
        _messages.value = current
    }
}
