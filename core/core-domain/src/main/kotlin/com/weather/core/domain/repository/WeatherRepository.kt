package com.weather.core.domain.repository

import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.model.DailyForecast

// ════════════════════════════════════════════════════════
//  Clean Architecture — Domain 層（最內層，最純粹）
//
//  這裡只定義「需要什麼能力」，完全不知道資料從哪裡來。
//  真正的實作放在 core-data 的 WeatherRepositoryImpl，
//  由 Hilt 在執行時自動注入進來。
//
//  好處：如果將來換成其他 API 或加入本地快取，
//        只需要改實作，這裡的介面完全不動。
// ════════════════════════════════════════════════════════

interface WeatherRepository {

    // [Coroutines] suspend fun：
    // 代表這個函式會執行「耗時操作」（網路請求），
    // 必須在 Coroutine（協程）內才能呼叫。
    // 執行時會「暫停」這個協程，等結果回來再繼續，
    // 但不會阻塞 UI 主執行緒，App 依然保持流暢。
    suspend fun getCurrentWeather(city: City): Result<CurrentWeather>

    suspend fun getWeeklyForecast(city: City): Result<List<DailyForecast>>
}
