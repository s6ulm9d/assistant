package com.soulassistant.app.data.manager;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CommunicationManager_Factory implements Factory<CommunicationManager> {
  private final Provider<Context> contextProvider;

  public CommunicationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CommunicationManager get() {
    return newInstance(contextProvider.get());
  }

  public static CommunicationManager_Factory create(Provider<Context> contextProvider) {
    return new CommunicationManager_Factory(contextProvider);
  }

  public static CommunicationManager newInstance(Context context) {
    return new CommunicationManager(context);
  }
}
