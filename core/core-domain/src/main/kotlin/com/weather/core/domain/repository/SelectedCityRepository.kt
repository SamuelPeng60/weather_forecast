package com.weather.core.domain.repository

import com.weather.core.domain.model.City
import kotlinx.coroutines.flow.StateFlow

//  管理 今日天氣 週預報 城市列表三個頁面，共享同一個目前城市
interface SelectedCityRepository {

    // Coroutines StateFlow 多頁面共享狀態
    val selectedCity: StateFlow<City>

    // 更新 StateFlow 的值，所有訂閱者自動收到新城市
    fun selectCity(city: City)
}
