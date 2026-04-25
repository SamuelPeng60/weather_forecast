package com.weather.core.data.mapper

import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.model.DailyForecast
import com.weather.core.network.dto.CurrentDto
import com.weather.core.network.dto.DailyDto
import java.util.Calendar

fun CurrentDto.toDomain(cityName: String): CurrentWeather = CurrentWeather(
    cityName = cityName,
    temperature = temperature,
    feelsLike = feelsLike,
    humidity = humidity,
    windSpeed = windSpeed,
    weatherCode = weatherCode,
    description = weatherCode.toWeatherDescription(),
    emoji = weatherCode.toWeatherEmoji(),
    updateTime = time.replace("T", " ")
    // "2026-04-25T22:00" → "2026-04-25 22:00"
)

fun DailyDto.toDomain(): List<DailyForecast> { // 週預報轉換
    return time.indices.map { i ->
        DailyForecast(
            date = time[i],
            dayOfWeek = time[i].toDayOfWeekZh(),
            maxTemp = tempMax[i],
            minTemp = tempMin[i],
            weatherCode = weatherCode[i],
            description = weatherCode[i].toWeatherDescription(),
            emoji = weatherCode[i].toWeatherEmoji(),
            precipitationSum = precipitationSum.getOrElse(i) { 0.0 }
        )
    }
}

private fun String.toDayOfWeekZh(): String {
    val parts = split("-") //  "2026-04-25" → ["2026", "04", "25"]
    if (parts.size < 3) return ""
    val cal = Calendar.getInstance().apply {
        // Calendar 是 0~6，所以月份要-1
        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
    }
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "週一"
        Calendar.TUESDAY -> "週二"
        Calendar.WEDNESDAY -> "週三"
        Calendar.THURSDAY -> "週四"
        Calendar.FRIDAY -> "週五"
        Calendar.SATURDAY -> "週六"
        Calendar.SUNDAY -> "週日"
        else -> ""
    }
}

fun Int.toWeatherDescription(): String = when (this) {
    0 -> "晴天"
    1 -> "大致晴朗"
    2 -> "局部多雲"
    3 -> "多雲"
    45, 48 -> "霧"
    51, 53 -> "毛毛雨"
    55 -> "濃毛毛雨"
    61, 63 -> "雨"
    65 -> "大雨"
    71, 73 -> "雪"
    75 -> "大雪"
    77 -> "雪粒"
    80, 81 -> "陣雨"
    82 -> "大陣雨"
    85, 86 -> "陣雪"
    95 -> "雷雨"
    96, 99 -> "冰雹雷雨"
    else -> "未知"
}

fun Int.toWeatherEmoji(): String = when (this) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    51, 53, 55 -> "🌦️"
    61, 63, 65, 80, 81, 82 -> "🌧️"
    71, 73, 75, 77, 85, 86 -> "🌨️"
    95, 96, 99 -> "⛈️"
    else -> "🌡️"
}
