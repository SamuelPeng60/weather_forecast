package com.weather.core.network.di

import com.weather.core.network.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// ════════════════════════════════════════════════════════
//  Clean Architecture — Network 層 DI 設定
//
//  用 @Provides 告訴 Hilt 如何建立網路相關的物件。
//  建立順序：OkHttpClient → Retrofit → WeatherApiService
// ════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.open-meteo.com/"

    // @Provides：告訴 Hilt 這個函式負責建立 OkHttpClient
    // @Singleton：整個 App 只建立一次，重複使用
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            // LoggingInterceptor：在 Logcat 印出每次 HTTP 請求和回應，方便除錯
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    // Hilt 自動把上面建立的 OkHttpClient 注入進來
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        // GsonConverterFactory：自動把 JSON 字串轉成 Kotlin data class
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // [Coroutines] Retrofit 搭配 suspend fun 使用時，
    // 會自動在背景執行緒發出請求，不需要手動切換執行緒
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)
}
