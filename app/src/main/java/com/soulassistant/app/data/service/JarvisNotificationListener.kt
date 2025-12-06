package com.soulassistant.app.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Singleton
class JarvisNotificationListener : NotificationListenerService() {

    companion object {
        private val _notifications = MutableStateFlow<List<String>>(emptyList())
        val notifications: StateFlow<List<String>> = _notifications
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")?.toString()

        if (title != null && text != null) {
            Log.d("JarvisNotif", "Posted: $packageName - $title: $text")
            val current = _notifications.value.toMutableList()
            current.add("[$packageName] $title: $text")
            if (current.size > 20) current.removeAt(0) // Keep last 20
            _notifications.value = current
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removal if needed
    }
}
