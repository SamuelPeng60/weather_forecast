package com.weather.core.domain.repository

import com.weather.core.domain.model.City
import kotlinx.coroutines.flow.StateFlow

// ════════════════════════════════════════════════════════
//  Clean Architecture — Domain 層
//
//  管理「目前選擇的城市」的 Repository 介面。
//  今日天氣、週預報、城市列表三個頁面都透過這個介面
//  共享同一個城市狀態，切換城市時三個頁面都會同步更新。
// ════════════════════════════════════════════════════════

interface SelectedCityRepository {

    // [Coroutines — StateFlow]
    // StateFlow 是 Coroutines 提供的「狀態流」：
    // - 永遠持有一個最新值（目前選中的城市）
    // - 任何訂閱者（ViewModel）都能即時收到變化
    // - 適合用來在多個頁面之間共享狀態
    val selectedCity: StateFlow<City>

    // 切換城市：更新 StateFlow 的值，所有訂閱者自動收到新城市
    fun selectCity(city: City)
}
