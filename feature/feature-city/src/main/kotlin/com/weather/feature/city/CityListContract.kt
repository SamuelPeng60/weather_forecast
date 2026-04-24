package com.weather.feature.city

import com.weather.core.domain.model.City

// ════════════════════════════════════════════════════════
//  Feature Module — feature-city
//  MVI 合約定義
//
//  城市列表頁面的特別之處：
//  選城市後，不只是更新自己的 state，
//  還會透過 SelectedCityRepository 的 StateFlow
//  通知其他頁面（today、weekly）自動刷新。
// ════════════════════════════════════════════════════════

object CityListContract {

    data class State(
        val cities: List<City> = emptyList(),
        val selectedCity: City? = null,
        // groupedCities：依國家分組的城市 Map，例如 {"台灣": [台北, 台中, 高雄]}
        // Map 的 key 是國家名，value 是該國城市清單
        val groupedCities: Map<String, List<City>> = emptyMap()
    )

    sealed class Intent {
        // 帶有資料的 Intent：使用者選了哪個城市
        data class SelectCity(val city: City) : Intent()
    }

    sealed class Effect {
        // 城市被選中後的一次性通知（可用於關閉頁面或顯示提示）
        data class CitySelected(val city: City) : Effect()
    }
}
