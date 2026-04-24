package com.weather.feature.today

import com.weather.core.domain.model.City
import com.weather.core.domain.model.CurrentWeather

// ════════════════════════════════════════════════════════
//  Feature Module — feature-today
//  MVI 架構的「合約」定義
//
//  MVI = Model + View + Intent
//  把一個頁面的所有可能狀態、使用者動作、副作用
//  集中定義在這裡，讓程式碼更容易理解與維護。
//
//  資料流方向（單向）：
//    使用者操作 → Intent → ViewModel → State → UI 更新
// ════════════════════════════════════════════════════════

object TodayWeatherContract {

    // State：UI 畫面的完整狀態快照
    // ViewModel 每次更新 state，Compose 就自動重新繪製畫面
    data class State(
        val isLoading: Boolean = true,       // 是否顯示載入中
        val currentWeather: CurrentWeather? = null, // 天氣資料（null 代表尚未載入）
        val selectedCity: City? = null,       // 目前選中的城市
        val error: String? = null            // 錯誤訊息（null 代表沒有錯誤）
    )

    // Intent：使用者可以做的動作
    // sealed class：限制只能是這裡定義的子類別，when 表達式可以窮舉
    sealed class Intent {
        object Refresh : Intent()  // 使用者點擊「重新整理」
    }

    // Effect：一次性的副作用，不會儲存在 State 中
    // 例如：顯示 Toast、播放音效、跳轉頁面
    // 用 SharedFlow 傳遞，不像 StateFlow 會保留最後一個值
    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}
