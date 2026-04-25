package com.weather.core.network.dto

import com.google.gson.annotations.SerializedName

/* 回傳的json
 {
    "latitude": 25.0478,
    "longitude": 121.5319,
    "timezone": "Asia/Taipei",
    "current": {
      "time": "2024-01-15T14:00",
      "temperature_2m": 18.5,
      "apparent_temperature": 16.2,
      "relative_humidity_2m": 72,
      "wind_speed_10m": 12.3,
      "weather_code": 2
    },
    "daily": {
      "time": ["2024-01-15", "2024-01-16", ...],
      "weather_code": [2, 1, 3, ...],
      "temperature_2m_max": [20.1, 22.3, ...],
      "temperature_2m_min": [14.5, 15.2, ...],
      "precipitation_sum": [0.0, 0.5, ...]
    }
  }
*/
data class WeatherResponseDto(
    @SerializedName("latitude") val latitude: Double, // 可拿掉
    @SerializedName("longitude") val longitude: Double, // 可拿掉
    @SerializedName("timezone") val timezone: String, // 可拿掉
    @SerializedName("current") val current: CurrentDto?, // 可拿掉
    @SerializedName("daily") val daily: DailyDto? // 可拿掉
)

data class CurrentDto(
    @SerializedName("time") val time: String, // 可拿掉
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val feelsLike: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("weather_code") val weatherCode: Int
)

data class DailyDto(
    @SerializedName("time") val time: List<String>, // 可拿掉
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>
)
