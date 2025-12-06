package com.soulassistant.app.domain.model

sealed class JarvisCommand {
    // App Management
    data class OpenApp(val packageName: String) : JarvisCommand()
    
    // UI Interaction (Accessibility)
    data class Tap(val text: String? = null, val contentDesc: String? = null) : JarvisCommand()
    data class InputText(val text: String) : JarvisCommand()
    object ScrollDown : JarvisCommand()
    object ScrollUp : JarvisCommand()
    object Back : JarvisCommand()
    object Home : JarvisCommand()
    object OpenNotifications : JarvisCommand()
    
    // Communication
    data class MakeCall(val number: String) : JarvisCommand()
    data class SendMessage(val recipient: String, val message: String, val channel: String = "sms") : JarvisCommand()
    
    // System
    data class CreateFolder(val folderName: String) : JarvisCommand()
    data class ToggleSetting(val setting: String, val enable: Boolean) : JarvisCommand()
    
    // Media
    data class PlayMusic(val query: String) : JarvisCommand()
    
    // Search
    data class Search(val query: String) : JarvisCommand()
}
