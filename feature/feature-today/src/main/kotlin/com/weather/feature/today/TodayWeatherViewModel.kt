package com.weather.feature.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import com.weather.core.domain.usecase.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Feature Module — feature-today
//  MVI 架構的 ViewModel（商業邏輯層）
//
//  ViewModel 的職責：
//  1. 訂閱「選中城市」的變化
//  2. 城市變化時自動呼叫 Use Case 載入天氣
//  3. 更新 State 通知 UI 重新繪製
// ════════════════════════════════════════════════════════

// @HiltViewModel：讓 Hilt 知道這個 ViewModel 需要依賴注入
// @Inject constructor：告訴 Hilt 怎麼建立這個 ViewModel
@HiltViewModel
class TodayWeatherViewModel @Inject constructor(
    // [Clean Architecture] 依賴 Use Case，不直接呼叫 Repository
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val selectedCityRepository: SelectedCityRepository
) : ViewModel() {

    // [Coroutines — MutableStateFlow] 私有可寫的狀態
    private val _state = MutableStateFlow(TodayWeatherContract.State())
    // 對外只暴露唯讀的 StateFlow，Compose 訂閱這個來更新畫面
    val state: StateFlow<TodayWeatherContract.State> = _state.asStateFlow()

    // [Coroutines — MutableSharedFlow] 用於一次性事件（Effect）
    // SharedFlow 不會保留舊值，適合「顯示一次 Toast」這類操作
    private val _effect = MutableSharedFlow<TodayWeatherContract.Effect>()
    val effect: SharedFlow<TodayWeatherContract.Effect> = _effect.asSharedFlow()

    init {
        // [Coroutines] viewModelScope.launch：
        // 在 ViewModel 的生命週期內啟動一個協程
        // ViewModel 被清除時，這個協程會自動取消，不會造成記憶體洩漏
        viewModelScope.launch {
            // [Coroutines] collect：持續監聽 StateFlow 的值
            // 每當城市切換，這裡就會收到新城市並重新載入天氣
            selectedCityRepository.selectedCity.collect { city ->
                loadWeather(city)
            }
        }
    }

    // 處理來自 UI 的使用者動作（Intent）
    fun processIntent(intent: TodayWeatherContract.Intent) {
        when (intent) {
            is TodayWeatherContract.Intent.Refresh -> {
                val city = _state.value.selectedCity ?: return
                loadWeather(city)
            }
        }
    }

    private fun loadWeather(city: City) {
        // [Coroutines] launch：啟動新協程執行網路請求
        // 不會阻塞 UI，請求進行中 App 仍然可以回應使用者操作
        viewModelScope.launch {
            // update{}：原子性地更新 StateFlow，Compose 收到後重新繪製
            _state.update { it.copy(isLoading = true, error = null, selectedCity = city) }

            // [Coroutines] 呼叫 suspend fun，在這裡「暫停」等待網路回應
            when (val result = getCurrentWeatherUseCase(city)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, currentWeather = result.data) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    // emit：發送一次性事件給 UI（例如顯示 Snackbar）
                    _effect.emit(TodayWeatherContract.Effect.ShowError(result.message))
                }
            }
        }
    }
}
