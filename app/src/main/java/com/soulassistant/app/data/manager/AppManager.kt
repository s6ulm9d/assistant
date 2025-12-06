package com.soulassistant.app.data.manager

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun openApp(appIdentifier: String): Result<String> {
        return try {
            val packageName = resolvePackageName(appIdentifier)
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Result.success("Opened $appIdentifier")
            } else {
                Result.failure(Exception("App not found: $appIdentifier"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun resolvePackageName(appIdentifier: String): String {
        // Map common app names to package names
        return when (appIdentifier.lowercase()) {
            "whatsapp" -> "com.whatsapp"
            "spotify" -> "com.spotify.music"
            "youtube" -> "com.google.android.youtube"
            "chrome" -> "com.android.chrome"
            "gmail" -> "com.google.android.gm"
            "maps" -> "com.google.android.apps.maps"
            "instagram" -> "com.instagram.android"
            "facebook" -> "com.facebook.katana"
            "twitter", "x" -> "com.twitter.android"
            "telegram" -> "org.telegram.messenger"
            "snapchat" -> "com.snapchat.android"
            "tiktok" -> "com.zhiliaoapp.musically"
            "netflix" -> "com.netflix.mediaclient"
            "amazon" -> "com.amazon.mShop.android.shopping"
            "settings" -> "com.android.settings"
            "camera" -> "com.android.camera2"
            "gallery", "photos" -> "com.google.android.apps.photos"
            "calculator" -> "com.google.android.calculator"
            "calendar" -> "com.google.android.calendar"
            "clock" -> "com.google.android.deskclock"
            else -> appIdentifier // Assume it's already a package name
        }
    }
    

    fun playMusic(query: String): Result<String> {
        return try {
            // Try Spotify URI approach first (direct play)
            val spotifyUri = "spotify:search:$query"
            val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri))
            spotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            val pm = context.packageManager
            try {
                pm.getPackageInfo("com.spotify.music", 0)
                // Spotify is installed, try to play directly
                context.startActivity(spotifyIntent)
                return Result.success("Playing $query on Spotify")
            } catch (e: Exception) {
                // Spotify not installed, fall back to generic media intent
            }

            // Fallback: Use MediaStore intent
            val intent = Intent(android.provider.MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
            intent.putExtra(android.provider.MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
            intent.putExtra(android.app.SearchManager.QUERY, query)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (intent.resolveActivity(pm) != null) {
                context.startActivity(intent)
                Result.success("Playing $query")
            } else {
                // Last resort: Web search
                val searchIntent = Intent(Intent.ACTION_WEB_SEARCH)
                searchIntent.putExtra(android.app.SearchManager.QUERY, "play $query on spotify")
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(searchIntent)
                Result.success("Searching for $query")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
