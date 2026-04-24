package com.weather.core.data.di

import com.weather.core.data.repository.SelectedCityRepositoryImpl
import com.weather.core.data.repository.WeatherRepositoryImpl
import com.weather.core.domain.repository.SelectedCityRepository
import com.weather.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ════════════════════════════════════════════════════════
//  Clean Architecture — Data 層 DI 設定
//
//  Hilt（依賴注入框架）在這裡學習：
//  「當有人需要 WeatherRepository 介面時，給他 WeatherRepositoryImpl」
//
//  這樣 ViewModel 只需要宣告「我需要 WeatherRepository」，
//  不需要自己 new 物件，完全解耦。
// ════════════════════════════════════════════════════════

// @Module：告訴 Hilt 這是一個提供依賴的模組
// @InstallIn(SingletonComponent::class)：這些依賴在整個 App 生命週期內有效
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // @Binds：將介面（WeatherRepository）綁定到實作（WeatherRepositoryImpl）
    // @Singleton：確保整個 App 只建立一個實例
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindSelectedCityRepository(impl: SelectedCityRepositoryImpl): SelectedCityRepository
}
