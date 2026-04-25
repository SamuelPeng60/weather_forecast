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


// @InstallIn(SingletonComponent::class)：這些依賴在整個 App 生命週期內有效
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // @Binds：將介面 WeatherRepository -> WeatherRepositoryImpl
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindSelectedCityRepository(impl: SelectedCityRepositoryImpl): SelectedCityRepository
}
