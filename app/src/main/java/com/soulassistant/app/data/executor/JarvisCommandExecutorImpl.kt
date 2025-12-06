package com.soulassistant.app.data.executor

import com.soulassistant.app.data.manager.AppManager
import com.soulassistant.app.data.manager.CommunicationManager
import com.soulassistant.app.data.manager.SystemManager
import com.soulassistant.app.data.service.JarvisAccessibilityService
import com.soulassistant.app.domain.executor.JarvisCommandExecutor
import com.soulassistant.app.domain.model.JarvisCommand
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JarvisCommandExecutorImpl @Inject constructor(
    private val appManager: AppManager,
    private val communicationManager: CommunicationManager,
    private val systemManager: SystemManager,
    private val searchManager: com.soulassistant.app.data.manager.SearchManager
) : JarvisCommandExecutor {

    override suspend fun execute(commands: List<JarvisCommand>): String {
        val results = StringBuilder()
        
        for (command in commands) {
            val result = when (command) {
                is JarvisCommand.OpenApp -> {
                    appManager.openApp(command.packageName).fold(
                        onSuccess = { 
                            // Wait for app to fully load
                            delay(2000)
                            "Opened ${command.packageName}" 
                        },
                        onFailure = { "Failed to open ${command.packageName}: ${it.message}" }
                    )
                }
                is JarvisCommand.Tap -> {
                    val service = JarvisAccessibilityService.instance
                    if (service != null) {
                        // Wait a bit for UI to be ready
                        delay(1500)
                        val success = if (command.text != null) {
                            service.tapByText(command.text)
                        } else if (command.contentDesc != null) {
                            service.tapByContentDesc(command.contentDesc)
                        } else {
                            false
                        }
                        if (success) "Tapped ${command.text ?: command.contentDesc}" else "Could not find element to tap"
                    } else {
                        "Accessibility Service not enabled"
                    }
                }
                is JarvisCommand.InputText -> {
                    val service = JarvisAccessibilityService.instance
                    if (service != null) {
                        delay(1000)
                        if (service.inputTextIntoFocusedField(command.text)) "Typed '${command.text}'" else "Could not type text"
                    } else {
                        "Accessibility Service not enabled"
                    }
                }
                is JarvisCommand.ScrollDown -> {
                    val service = JarvisAccessibilityService.instance
                    delay(500)
                    if (service?.scroll(1) == true) "Scrolled down" else "Could not scroll"
                }
                is JarvisCommand.ScrollUp -> {
                    val service = JarvisAccessibilityService.instance
                    delay(500)
                    if (service?.scroll(-1) == true) "Scrolled up" else "Could not scroll"
                }
                is JarvisCommand.Back -> {
                    val service = JarvisAccessibilityService.instance
                    if (service?.performGlobalBack() == true) "Went back" else "Could not go back"
                }
                is JarvisCommand.Home -> {
                    val service = JarvisAccessibilityService.instance
                    if (service?.performGlobalHome() == true) "Went home" else "Could not go home"
                }
                is JarvisCommand.OpenNotifications -> {
                    val service = JarvisAccessibilityService.instance
                    if (service?.openNotificationsPanel() == true) "Opened notifications" else "Could not open notifications"
                }
                is JarvisCommand.MakeCall -> {
                    communicationManager.makeCall(command.number).fold(
                        onSuccess = { "Calling ${command.number}" },
                        onFailure = { "Failed to call: ${it.message}" }
                    )
                }
                is JarvisCommand.SendMessage -> {
                    communicationManager.sendMessage(command.recipient, command.message, command.channel).fold(
                        onSuccess = { "Sent message to ${command.recipient}" },
                        onFailure = { "Failed to send message: ${it.message}" }
                    )
                }
                is JarvisCommand.CreateFolder -> {
                    systemManager.createFolder(command.folderName).fold(
                        onSuccess = { "Created folder ${command.folderName}" },
                        onFailure = { "Failed to create folder: ${it.message}" }
                    )
                }
                is JarvisCommand.ToggleSetting -> {
                    when (command.setting.toLowerCase()) {
                        "wifi" -> systemManager.toggleWifi(command.enable).getOrDefault("Failed to toggle WiFi")
                        "bluetooth" -> systemManager.toggleBluetooth(command.enable).getOrDefault("Failed to toggle Bluetooth")
                        "flashlight" -> systemManager.toggleFlashlight(command.enable).getOrDefault("Failed to toggle Flashlight")
                        else -> "Unknown setting ${command.setting}"
                    }
                }
                is JarvisCommand.PlayMusic -> {
                    appManager.playMusic(command.query).fold(
                        onSuccess = { 
                            delay(3000) // Wait for Spotify to load search results
                            
                            // Try to tap the first result automatically
                            val service = JarvisAccessibilityService.instance
                            if (service != null) {
                                // Look for common play button texts/descriptions
                                val playAttempts = listOf(
                                    "Play",
                                    "play",
                                    command.query, // The song name itself
                                    "Track" // Spotify uses this
                                )
                                
                                var played = false
                                for (attempt in playAttempts) {
                                    if (service.tapByText(attempt)) {
                                        played = true
                                        break
                                    }
                                }
                                
                                if (played) {
                                    "Now playing ${command.query}"
                                } else {
                                    "Opened ${command.query} in Spotify - tap to play"
                                }
                            } else {
                                "Playing ${command.query}"
                            }
                        },
                        onFailure = { "Failed to play music: ${it.message}" }
                    )
                }
                is JarvisCommand.Search -> {
                    val searchResults = searchManager.search(command.query)
                    if (searchResults.isEmpty()) {
                        "No results found for '${command.query}'"
                    } else {
                        "Found ${searchResults.size} results:\n" + searchResults.take(5).joinToString("\n")
                    }
                }
            }
            results.append(result).append("\n")
            // Small delay between actions
            delay(500) 
        }
        return results.toString().trim()
    }
}
