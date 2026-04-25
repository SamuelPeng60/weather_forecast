package com.weather.core.domain.usecase

import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.model.DailyForecast
import com.weather.core.domain.repository.WeatherRepository
import javax.inject.Inject

//  取得一週預報
class GetWeeklyForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    // [Coroutines] suspend：呼叫時會等待網路結果，不阻塞主執行緒
    suspend operator fun invoke(city: City): Result<List<DailyForecast>> =
        repository.getWeeklyForecast(city)
}
