package com.soulassistant.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.soulassistant.app.data.model.*
import com.soulassistant.app.data.remote.GeminiService
import com.soulassistant.app.domain.repository.GeminiRepository
import javax.inject.Inject

import com.soulassistant.app.utils.Secrets

class GeminiRepositoryImpl @Inject constructor(
    private val apiService: GeminiService
) : GeminiRepository {

    private val API_KEY = Secrets.API_KEY

    override suspend fun generateContent(prompt: String): Result<AiResponse> {
        return try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(Part(text = prompt + "\n\n" + SYSTEM_PROMPT))
                    )
                )
            )

            val response = apiService.generateContent(API_KEY, request)
            
            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    val aiResponse = parseJson(text)
                    Result.success(aiResponse)
                } else {
                    Result.failure(Exception("Empty response from AI"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseJson(text: String): AiResponse {
        try {
            val cleanText = text.replace("```json", "").replace("```", "").trim()
            return Gson().fromJson(cleanText, AiResponse::class.java)
        } catch (e: Exception) {
            Log.e("GeminiRepo", "JSON Parse Error: $text", e)
            // Fallback for non-JSON response (shouldn't happen with strict prompt)
            return AiResponse(
                action = "GENERAL_ANSWER",
                actions = null,
                params = emptyMap(),
                message = text
            )
        }
    }

    companion object {
        private const val SYSTEM_PROMPT = """
            You are Jarvis, an advanced device automation assistant like Google Assistant.
            You MUST respond in STRICT JSON format.
            
            For COMPLEX multi-step tasks, return an "actions" array. For SIMPLE single-step tasks, return a single "action".
            
            Available Actions:
            1. OPEN_APP(app_name: string)
            2. TAP(text: string)
            3. TYPE(text: string)
            4. SCROLL_DOWN(), SCROLL_UP()
            5. BACK(), HOME()
            6. CALL(number: string)
            7. SEND_MESSAGE(recipient, message, channel)
            8. TOGGLE_WIFI/BLUETOOTH/FLASHLIGHT(enable: boolean)
            9. CREATE_FOLDER(name: string)
            10. PLAY_MUSIC(query: string)
            11. SEARCH(query: string)
            12. GENERAL_ANSWER()
            
            SINGLE-STEP Examples:
            User: "Open WhatsApp"
            {"action": "OPEN_APP", "params": {"app_name": "whatsapp"}, "message": "Opening WhatsApp"}
            
            User: "Call Mom"
            {"action": "CALL", "params": {"number": "Mom"}, "message": "Calling Mom"}
            
            MULTI-STEP Examples (use "actions" array):
            User: "Send message to John on WhatsApp saying Hello"
            {
              "actions": [
                {"action": "OPEN_APP", "params": {"app_name": "whatsapp"}},
                {"action": "TAP", "params": {"text": "Search"}},
                {"action": "TYPE", "params": {"text": "John"}},
                {"action": "TAP", "params": {"text": "John"}},
                {"action": "TYPE", "params": {"text": "Hello"}},
                {"action": "TAP", "params": {"text": "Send"}}
              ],
              "message": "Sending message to John"
            }
            
            User: "Search for cat videos on YouTube"
            {
              "actions": [
                {"action": "OPEN_APP", "params": {"app_name": "youtube"}},
                {"action": "TAP", "params": {"text": "Search"}},
                {"action": "TYPE", "params": {"text": "cat videos"}}
              ],
              "message": "Searching for cat videos"
            }
            
            IMPORTANT:
            - Use "actions" array for multi-step flows
            - Use single "action" for simple one-step commands
            - Always include "message" for voice feedback
            - For apps, accept friendly names (WhatsApp, Spotify, YouTube)
        """
    }
}
