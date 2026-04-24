package com.weather.core.data.repository

import com.weather.core.domain.model.Cities
import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════════════════
//  Clean Architecture — Data 層
//  SelectedCityRepository 介面的實作
// ════════════════════════════════════════════════════════

// @Singleton：整個 App 只有一個實例
// 這非常重要！如果不是 Singleton，每個頁面會有各自的實例，
// 城市切換就無法同步給其他頁面。
@Singleton
class SelectedCityRepositoryImpl @Inject constructor() : SelectedCityRepository {

    // [Coroutines — MutableStateFlow]
    // MutableStateFlow：可以寫入新值的狀態流
    // 初始值設為「台北」（Cities.defaultCity）
    // 命名慣例：私有的可寫版本用底線開頭 `_selectedCity`
    private val _selectedCity = MutableStateFlow(Cities.defaultCity)

    // 對外只暴露唯讀的 StateFlow，防止外部直接修改
    // asStateFlow()：將 MutableStateFlow 轉成唯讀的 StateFlow
    override val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    override fun selectCity(city: City) {
        // 更新 StateFlow 的值，所有 collect{} 的地方會立即收到通知
        _selectedCity.value = city
    }
}
