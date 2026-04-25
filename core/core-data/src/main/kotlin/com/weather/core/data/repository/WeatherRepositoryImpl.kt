package com.weather.core.data.repository

import com.weather.core.common.Result
import com.weather.core.data.mapper.toDomain
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.model.DailyForecast
import com.weather.core.domain.repository.WeatherRepository
import com.weather.core.network.api.WeatherApiService
import javax.inject.Inject

//  這層知道資料從哪裡來（WeatherApiService 網路請求），
//  但 Domain 層的使用者（ViewModel、UseCase）完全不知道。

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {

    // 用 try catch 包住網路呼叫，發生錯誤時不會直接crash
    override suspend fun getCurrentWeather(city: City): Result<CurrentWeather> {
        return try {
            // apiService.getWeather() 是 suspend fun
            val response = apiService.getWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone
            )
            val current = response.current
                ?: return Result.Error("無法取得目前天氣資料")
            Result.Success(current.toDomain(city.nameZh))
        } catch (e: Exception) {
            Result.Error(e.message ?: "網路連線錯誤", e)
        }
    }

    override suspend fun getWeeklyForecast(city: City): Result<List<DailyForecast>> {
        return try {
            val response = apiService.getWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone
            )
            val daily = response.daily
                ?: return Result.Error("無法取得週預報資料")
            Result.Success(daily.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "網路連線錯誤", e)
        }
    }
}
