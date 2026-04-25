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

//  用 @Provides 告訴 Hilt 順序：OkHttpClient -> Retrofit -> WeatherApiService
@Module
@InstallIn(SingletonComponent::class) // 在 App 整個生命週期有效
object NetworkModule {

    private const val BASE_URL = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY  // 印出完整 HTTP 內容
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        // 直接JSON 字串轉成 data class
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Coroutines 加上 suspend fun ->自動背景執行緒發出請求
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)
}
