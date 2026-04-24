package com.weather.core.domain.model

data class DailyForecast(
    val date: String,
    val dayOfWeek: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int,
    val description: String,
    val emoji: String,
    val precipitationSum: Double
)
