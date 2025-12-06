package com.soulassistant.app.data.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        var instance: JarvisAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("JarvisAccess", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Optional: Log events or track current app
    }

    override fun onInterrupt() {
        Log.d("JarvisAccess", "Service Interrupted")
        instance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    // --- Helper Methods ---

    fun tapByText(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        Log.d("JarvisAccess", "Attempting to tap by text: '$text'")
        // Use recursive fuzzy search instead of strict findAccessibilityNodeInfosByText
        return findAndClick(root, text)
    }

    fun tapByContentDesc(desc: String): Boolean {
        val root = rootInActiveWindow ?: return false
        Log.d("JarvisAccess", "Attempting to tap by desc: '$desc'")
        return findAndClick(root, desc)
    }

    private fun findAndClick(node: AccessibilityNodeInfo?, query: String): Boolean {
        if (node == null) return false

        val nodeText = node.text?.toString()
        val nodeDesc = node.contentDescription?.toString()
        
        // Log visible nodes for debugging (optional, can be noisy)
        // if (!nodeText.isNullOrBlank()) Log.d("JarvisAccess", "Node: '$nodeText' (Clickable: ${node.isClickable})")

        val matchesText = nodeText?.contains(query, ignoreCase = true) == true
        val matchesDesc = nodeDesc?.contains(query, ignoreCase = true) == true

        if (matchesText || matchesDesc) {
            Log.d("JarvisAccess", "Found match: '$nodeText' / '$nodeDesc'")
            if (node.isClickable) {
                val result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("JarvisAccess", "Clicked node directly: $result")
                node.recycle()
                return result
            } else {
                // Try parent
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable) {
                        val result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        Log.d("JarvisAccess", "Clicked parent: $result")
                        parent.recycle()
                        node.recycle()
                        return result
                    }
                    parent = parent.parent
                }
            }
        }

        for (i in 0 until node.childCount) {
            if (findAndClick(node.getChild(i), query)) {
                node.recycle()
                return true
            }
        }
        node.recycle()
        return false
    }

    fun inputTextIntoFocusedField(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        if (focused != null && focused.isEditable) {
            val arguments = android.os.Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            val result = focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            focused.recycle()
            return result
        }
        // Fallback: Try to find any editable field if focus isn't set
        return inputTextRecursive(root, text)
    }

    private fun inputTextRecursive(node: AccessibilityNodeInfo?, text: String): Boolean {
        if (node == null) return false
        
        if (node.isEditable) {
             val arguments = android.os.Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            val result = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            node.recycle()
            return result
        }

        for (i in 0 until node.childCount) {
            if (inputTextRecursive(node.getChild(i), text)) {
                node.recycle()
                return true
            }
        }
        node.recycle()
        return false
    }

    fun performGlobalBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)
    fun performGlobalHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)
    fun openNotificationsPanel(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)

    fun scroll(direction: Int): Boolean {
        val root = rootInActiveWindow ?: return false
        val scrollable = findScrollableNode(root)
        if (scrollable != null) {
            val action = if (direction > 0) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
            val result = scrollable.performAction(action)
            scrollable.recycle()
            return result
        }
        return false
    }

    private fun findScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findScrollableNode(child)
            if (result != null) {
                if (child != result) child.recycle()
                return result
            }
            child?.recycle()
        }
        return null
    }
}
