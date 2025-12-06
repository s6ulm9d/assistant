package com.soulassistant.app.data.model

import com.google.gson.annotations.SerializedName

data class AiResponse(
    @SerializedName("action") val action: String?,
    @SerializedName("actions") val actions: List<ActionStep>?, // For multi-step automation
    @SerializedName("params") val params: Map<String, Any>?,
    @SerializedName("message") val message: String?
)

data class ActionStep(
    @SerializedName("action") val action: String,
    @SerializedName("params") val params: Map<String, Any>
)

enum class ActionType {
    OPEN_APP,
    SEND_MESSAGE,
    TOGGLE_WIFI,
    TOGGLE_BLUETOOTH,
    FLASHLIGHT,
    READ_NOTIFICATIONS,
    GENERAL_ANSWER
}
