package com.weather.feature.weekly

import com.weather.core.domain.model.City
import com.weather.core.domain.model.DailyForecast

// ════════════════════════════════════════════════════════
//  Feature Module — feature-weekly
//  MVI 架構的「合約」定義（與 feature-today 結構相同）
//
//  每個 Feature Module 都有自己的 Contract / ViewModel / Screen，
//  彼此完全獨立，可以單獨開發、測試、甚至移除。
//  這就是 Feature Module 的核心好處。
// ════════════════════════════════════════════════════════

object WeeklyForecastContract {

    // 週預報頁面的完整 UI 狀態
    data class State(
        val isLoading: Boolean = true,
        val forecasts: List<DailyForecast> = emptyList(), // 七天預報清單
        val selectedCity: City? = null,
        val error: String? = null
    )

    // 使用者可以做的動作
    sealed class Intent {
        object Refresh : Intent()
    }

    // 一次性副作用（顯示錯誤提示）
    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}
