package com.soulassistant.app.domain.repository

import com.soulassistant.app.data.model.AiResponse

interface GeminiRepository {
    suspend fun generateContent(prompt: String): Result<AiResponse>
}
