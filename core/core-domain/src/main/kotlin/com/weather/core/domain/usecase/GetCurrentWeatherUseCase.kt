package com.weather.core.domain.usecase

import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.repository.WeatherRepository
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Clean Architecture — Use Case（Domain 層）
//
//  Use Case 是「一個業務動作」的封裝，代表 App 能做的一件事。
//  這裡的動作是：「取得今日天氣」。
//
//  為什麼要有 Use Case，不直接從 ViewModel 呼叫 Repository？
//  → 如果未來「取得今日天氣」需要加入快取邏輯、權限檢查等，
//    只需改這個 Use Case，不影響 ViewModel 或 Repository。
// ════════════════════════════════════════════════════════

class GetCurrentWeatherUseCase @Inject constructor(
    // [Clean Architecture] 依賴介面，不依賴實作
    // 實際注入的是 WeatherRepositoryImpl（由 Hilt 決定）
    private val repository: WeatherRepository
) {
    // operator fun invoke：讓這個類別可以像函式一樣呼叫
    // 例如：val result = getCurrentWeatherUseCase(city)
    // [Coroutines] suspend：需在協程中呼叫，會等待網路結果
    suspend operator fun invoke(city: City): Result<CurrentWeather> =
        repository.getCurrentWeather(city)
}
