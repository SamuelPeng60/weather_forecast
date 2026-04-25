package com.weather.core.domain.usecase

import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.repository.WeatherRepository
import javax.inject.Inject

//  取得今日天氣
//  未來「取得今日天氣」需要加入快取邏輯、權限檢查等，只需改這個 Use Case，不影響 ViewModel 或 Repository

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: City): Result<CurrentWeather> =
        repository.getCurrentWeather(city)
}
