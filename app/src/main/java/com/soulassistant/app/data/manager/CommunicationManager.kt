package com.soulassistant.app.data.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunicationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun sendMessage(recipient: String, message: String, channel: String): Result<String> {
        return when (channel.toLowerCase()) {
            "whatsapp" -> sendWhatsApp(recipient, message)
            "sms" -> sendSMS(recipient, message)
            else -> Result.failure(Exception("Unknown channel: $channel"))
        }
    }

    private fun sendSMS(phone: String, message: String): Result<String> {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phone, null, message, null, null)
            Result.success("SMS sent to $phone")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sendWhatsApp(phone: String, message: String): Result<String> {
        return try {
            val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success("Opened WhatsApp for $phone")
        } catch (e: Exception) {
            // Fallback to browser if app not found (though package check handles it)
             val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
             val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             context.startActivity(intent)
             Result.success("Opened WhatsApp Web")
        }
    }
    fun makeCall(input: String): Result<String> {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return Result.failure(Exception("CALL_PHONE permission not granted"))
        }

        return try {
            var number = input
            // If input looks like a name (contains letters), try to resolve it
            if (input.any { it.isLetter() }) {
                val resolved = resolveContact(input)
                if (resolved != null) {
                    number = resolved
                } else {
                    return Result.failure(Exception("Contact not found: $input"))
                }
            }

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Result.success("Calling $number")
        } catch (e: SecurityException) {
            Result.failure(Exception("Permission denied: Call Phone"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun resolveContact(name: String): String? {
        val uri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
        // Use LIKE %name% for fuzzy matching
        val selection = "${android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")
        
        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
