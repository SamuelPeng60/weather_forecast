package com.weather.core.network.api

import com.weather.core.network.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    /*
        https://api.open-meteo.com/v1/forecast
        ?latitude=25.0478
        &longitude=121.5319
        &current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code
        &daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum
        &timezone=Asia/Taipei
        &forecast_days=7
    */
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum",
        @Query("timezone") timezone: String,
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponseDto
}
