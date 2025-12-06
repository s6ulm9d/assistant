package com.soulassistant.app.di

import com.soulassistant.app.data.executor.JarvisCommandExecutorImpl
import com.soulassistant.app.domain.executor.JarvisCommandExecutor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExecutorModule {

    @Binds
    @Singleton
    abstract fun bindJarvisCommandExecutor(
        impl: JarvisCommandExecutorImpl
    ): JarvisCommandExecutor
}
