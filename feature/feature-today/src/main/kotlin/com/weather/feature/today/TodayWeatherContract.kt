package com.weather.feature.today

import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather

// MVI contract
object TodayWeatherContract {

    // State：UI 畫面的完整狀態快照
    // ViewModel 每次更新 state，Compose 就自動重新繪製畫面
    data class State(
        val isLoading: Boolean = true,       // 是否顯示載入中
        val currentWeather: CurrentWeather? = null,
        val selectedCity: City? = null,
        val error: String? = null
    )

    sealed class Intent {
        object Refresh : Intent()  // 使用者點擊「重新整理」
    }

    // Effect：SharedFlow 傳遞 事件
    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}
