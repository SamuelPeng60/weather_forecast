package com.weather.core.domain.model

data class CurrentWeather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val description: String,
    val emoji: String,
    val updateTime: String
)
