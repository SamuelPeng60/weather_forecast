package com.weather.feature.weekly

import com.weather.core.domain.model.City
import com.weather.core.domain.model.DailyForecast

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

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}
