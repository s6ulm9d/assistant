package com.soulassistant.app.data.executor;

import com.soulassistant.app.data.manager.AppManager;
import com.soulassistant.app.data.manager.CommunicationManager;
import com.soulassistant.app.data.manager.SearchManager;
import com.soulassistant.app.data.manager.SystemManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class JarvisCommandExecutorImpl_Factory implements Factory<JarvisCommandExecutorImpl> {
  private final Provider<AppManager> appManagerProvider;

  private final Provider<CommunicationManager> communicationManagerProvider;

  private final Provider<SystemManager> systemManagerProvider;

  private final Provider<SearchManager> searchManagerProvider;

  public JarvisCommandExecutorImpl_Factory(Provider<AppManager> appManagerProvider,
      Provider<CommunicationManager> communicationManagerProvider,
      Provider<SystemManager> systemManagerProvider,
      Provider<SearchManager> searchManagerProvider) {
    this.appManagerProvider = appManagerProvider;
    this.communicationManagerProvider = communicationManagerProvider;
    this.systemManagerProvider = systemManagerProvider;
    this.searchManagerProvider = searchManagerProvider;
  }

  @Override
  public JarvisCommandExecutorImpl get() {
    return newInstance(appManagerProvider.get(), communicationManagerProvider.get(), systemManagerProvider.get(), searchManagerProvider.get());
  }

  public static JarvisCommandExecutorImpl_Factory create(Provider<AppManager> appManagerProvider,
      Provider<CommunicationManager> communicationManagerProvider,
      Provider<SystemManager> systemManagerProvider,
      Provider<SearchManager> searchManagerProvider) {
    return new JarvisCommandExecutorImpl_Factory(appManagerProvider, communicationManagerProvider, systemManagerProvider, searchManagerProvider);
  }

  public static JarvisCommandExecutorImpl newInstance(AppManager appManager,
      CommunicationManager communicationManager, SystemManager systemManager,
      SearchManager searchManager) {
    return new JarvisCommandExecutorImpl(appManager, communicationManager, systemManager, searchManager);
  }
}
