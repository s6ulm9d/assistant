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
public final class SystemManager_Factory implements Factory<SystemManager> {
  private final Provider<Context> contextProvider;

  public SystemManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SystemManager get() {
    return newInstance(contextProvider.get());
  }

  public static SystemManager_Factory create(Provider<Context> contextProvider) {
    return new SystemManager_Factory(contextProvider);
  }

  public static SystemManager newInstance(Context context) {
    return new SystemManager(context);
  }
}
