package com.soulassistant.app.data.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun toggleWifi(enable: Boolean): Result<String> {
        return try {
            // Android 10+ (Q) restricts programmatic WiFi toggling.
            // We must use Settings Panel or show a toast if not possible.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                panelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(panelIntent)
                Result.success("Opened WiFi Settings (Android 10+ restriction)")
            } else {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = enable
                Result.success("WiFi ${if (enable) "Enabled" else "Disabled"}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun toggleBluetooth(enable: Boolean): Result<String> {
        return try {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = bluetoothManager.adapter
            
            if (adapter == null) return Result.failure(Exception("No Bluetooth adapter"))

            // Note: Enabling/Disabling BT programmatically is deprecated/restricted in newer Android.
            // But we can try or open settings.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                 // Open Settings
                 val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                 context.startActivity(intent)
                 Result.success("Opened Bluetooth Settings")
            } else {
                @Suppress("DEPRECATION")
                if (enable) adapter.enable() else adapter.disable()
                Result.success("Bluetooth ${if (enable) "Enabled" else "Disabled"}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun toggleFlashlight(enable: Boolean): Result<String> {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0] // Usually back camera
            cameraManager.setTorchMode(cameraId, enable)
            Result.success("Flashlight ${if (enable) "On" else "Off"}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun createFolder(folderName: String): Result<String> {
        // Check for All Files Access on Android 11+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                return Result.failure(Exception("Missing 'All Files Access' permission"))
            }
        }

        return try {
            val path = if (folderName.startsWith("/")) folderName else "/$folderName"
            val targetDir = java.io.File(android.os.Environment.getExternalStorageDirectory(), path)

            if (targetDir.exists()) {
                return Result.success("Folder already exists: ${targetDir.absolutePath}")
            }

            if (targetDir.mkdirs()) {
                Result.success("Created folder: ${targetDir.absolutePath}")
            } else {
                Result.failure(Exception("Failed to create folder at ${targetDir.absolutePath}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
