package com.weather.core.domain.repository

import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.model.DailyForecast

//  這裡只做 interface，真正的實作在 core/data.repository/ WeatherRepositoryImpl
//  好處：如果將來換成其他 API 或加入本地快取，
//        只需要改實作，這裡的介面完全不動。

interface WeatherRepository {

    // Coroutines suspend fun -> 表示會有耗時請求
    suspend fun getCurrentWeather(city: City): Result<CurrentWeather>

    suspend fun getWeeklyForecast(city: City): Result<List<DailyForecast>>
}
