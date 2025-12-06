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
public final class AppManager_Factory implements Factory<AppManager> {
  private final Provider<Context> contextProvider;

  public AppManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppManager get() {
    return newInstance(contextProvider.get());
  }

  public static AppManager_Factory create(Provider<Context> contextProvider) {
    return new AppManager_Factory(contextProvider);
  }

  public static AppManager newInstance(Context context) {
    return new AppManager(context);
  }
}
