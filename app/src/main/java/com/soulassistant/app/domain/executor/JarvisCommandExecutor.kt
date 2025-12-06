package com.soulassistant.app.domain.executor

import com.soulassistant.app.domain.model.JarvisCommand

interface JarvisCommandExecutor {
    suspend fun execute(commands: List<JarvisCommand>): String
}
