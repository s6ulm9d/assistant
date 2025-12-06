package com.soulassistant.app.presentation;

import com.soulassistant.app.data.manager.TTSManager;
import com.soulassistant.app.domain.executor.JarvisCommandExecutor;
import com.soulassistant.app.domain.repository.GeminiRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<JarvisCommandExecutor> commandExecutorProvider;

  private final Provider<TTSManager> ttsManagerProvider;

  public ChatViewModel_Factory(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<JarvisCommandExecutor> commandExecutorProvider,
      Provider<TTSManager> ttsManagerProvider) {
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.commandExecutorProvider = commandExecutorProvider;
    this.ttsManagerProvider = ttsManagerProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(geminiRepositoryProvider.get(), commandExecutorProvider.get(), ttsManagerProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<JarvisCommandExecutor> commandExecutorProvider,
      Provider<TTSManager> ttsManagerProvider) {
    return new ChatViewModel_Factory(geminiRepositoryProvider, commandExecutorProvider, ttsManagerProvider);
  }

  public static ChatViewModel newInstance(GeminiRepository geminiRepository,
      JarvisCommandExecutor commandExecutor, TTSManager ttsManager) {
    return new ChatViewModel(geminiRepository, commandExecutor, ttsManager);
  }
}
