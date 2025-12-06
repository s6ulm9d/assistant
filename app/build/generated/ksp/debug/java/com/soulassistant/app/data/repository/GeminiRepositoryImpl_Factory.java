package com.soulassistant.app.data.repository;

import com.soulassistant.app.data.remote.GeminiService;
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
public final class GeminiRepositoryImpl_Factory implements Factory<GeminiRepositoryImpl> {
  private final Provider<GeminiService> apiServiceProvider;

  public GeminiRepositoryImpl_Factory(Provider<GeminiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public GeminiRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static GeminiRepositoryImpl_Factory create(Provider<GeminiService> apiServiceProvider) {
    return new GeminiRepositoryImpl_Factory(apiServiceProvider);
  }

  public static GeminiRepositoryImpl newInstance(GeminiService apiService) {
    return new GeminiRepositoryImpl(apiService);
  }
}
