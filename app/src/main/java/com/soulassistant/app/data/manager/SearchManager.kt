package com.soulassistant.app.data.manager

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun search(query: String): List<String> {
        val results = mutableListOf<String>()
        results.addAll(searchApps(query))
        results.addAll(searchContacts(query))
        results.addAll(searchFiles(query))
        return results
    }

    private fun searchApps(query: String): List<String> {
        val results = mutableListOf<String>()
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        for (pkg in packages) {
            val appName = pkg.applicationInfo.loadLabel(pm).toString()
            if (appName.contains(query, ignoreCase = true)) {
                results.add("📱 App: $appName")
            }
        }
        return results.take(3) // Limit to 3 apps
    }

    private fun searchContacts(query: String): List<String> {
        val results = mutableListOf<String>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                while (cursor.moveToNext() && results.size < 3) {
                    if (nameIndex >= 0 && numberIndex >= 0) {
                        val name = cursor.getString(nameIndex)
                        val number = cursor.getString(numberIndex)
                        results.add("👤 Contact: $name ($number)")
                    }
                }
            }
        } catch (e: Exception) {
            results.add("❌ Error searching contacts: ${e.message}")
        }
        return results
    }

    private fun searchFiles(query: String): List<String> {
        val results = mutableListOf<String>()
        
        // Search using MediaStore for media files
        val mediaResults = searchMediaStore(query)
        results.addAll(mediaResults)
        
        // Search file system for folders and other files
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (android.os.Environment.isExternalStorageManager()) {
                val fileResults = searchFileSystem(query)
                results.addAll(fileResults)
            } else {
                results.add("⚠️ Enable 'All Files Access' for complete file search")
            }
        }
        
        return results.take(10) // Limit to 10 files
    }

    private fun searchMediaStore(query: String): List<String> {
        val results = mutableListOf<String>()
        
        // Search for images, videos, audio, and documents
        val collections = listOf(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI to "🖼️",
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI to "🎥",
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to "🎵"
        )
        
        for ((uri, emoji) in collections) {
            try {
                val projection = arrayOf(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.DATA // Full path
                )
                val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
                val selectionArgs = arrayOf("%$query%")
                
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    val pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    
                    while (cursor.moveToNext() && results.size < 5) {
                        if (nameIndex >= 0 && pathIndex >= 0) {
                            val name = cursor.getString(nameIndex)
                            val path = cursor.getString(pathIndex)
                            results.add("$emoji $name\n   📂 $path")
                        }
                    }
                }
            } catch (e: Exception) {
                // Skip this collection if error
            }
        }
        
        return results
    }

    private fun searchFileSystem(query: String): List<String> {
        val results = mutableListOf<String>()
        val externalStorage = android.os.Environment.getExternalStorageDirectory()
        
        try {
            searchDirectory(externalStorage, query, results, maxDepth = 4)
        } catch (e: Exception) {
            results.add("❌ File search error: ${e.message}")
        }
        
        return results
    }

    private fun searchDirectory(dir: java.io.File, query: String, results: MutableList<String>, depth: Int = 0, maxDepth: Int = 4) {
        if (depth > maxDepth || results.size >= 10) return
        
        try {
            val files = dir.listFiles() ?: return
            
            for (file in files) {
                if (results.size >= 10) break
                
                if (file.name.contains(query, ignoreCase = true)) {
                    val icon = if (file.isDirectory) "📁" else "📄"
                    val type = if (file.isDirectory) "Folder" else "File"
                    results.add("$icon $type: ${file.name}\n   📂 ${file.absolutePath}")
                }
                
                // Recursively search subdirectories
                if (file.isDirectory && !file.name.startsWith(".")) {
                    searchDirectory(file, query, results, depth + 1, maxDepth)
                }
            }
        } catch (e: SecurityException) {
            // Skip directories we don't have permission to access
        }
    }
}
