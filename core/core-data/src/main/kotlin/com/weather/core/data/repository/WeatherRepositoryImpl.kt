package com.weather.core.data.repository

import com.weather.core.common.Result
import com.weather.core.data.mapper.toDomain
import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.domain.model.DailyForecast
import com.weather.core.domain.repository.WeatherRepository
import com.weather.core.network.api.WeatherApiService
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Clean Architecture — Data 層
//  WeatherRepository 介面的實作
//
//  這層知道資料從哪裡來（WeatherApiService 網路請求），
//  但 Domain 層的使用者（ViewModel、UseCase）完全不知道。
// ════════════════════════════════════════════════════════

class WeatherRepositoryImpl @Inject constructor(
    // [Clean Architecture] 依賴網路層的 API Service，由 Hilt 注入
    private val apiService: WeatherApiService
) : WeatherRepository {

    // [Coroutines] override suspend fun：
    // 實作 Domain 層定義的 suspend 函式
    // try/catch 包住網路呼叫，發生錯誤時回傳 Result.Error 而非 crash
    override suspend fun getCurrentWeather(city: City): Result<CurrentWeather> {
        return try {
            // apiService.getWeather() 是 suspend fun（Retrofit + Coroutines）
            // 這裡會「暫停」等待網路回應，不阻塞主執行緒
            val response = apiService.getWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone
            )
            val current = response.current
                // ?: 是 Elvis 運算子：若 current 為 null，直接回傳 Error
                ?: return Result.Error("無法取得目前天氣資料")
            // toDomain()：將網路回傳的 DTO 轉成 Domain Entity（見 WeatherMapper）
            Result.Success(current.toDomain(city.nameZh))
        } catch (e: Exception) {
            // 網路斷線、超時等例外統一轉成 Result.Error
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
