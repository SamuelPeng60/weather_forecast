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

    private val _state = MutableStateFlow(TodayWeatherContract.State())
    // 外部state: read only
    val state: StateFlow<TodayWeatherContract.State> = _state.asStateFlow()
    private val _effect = MutableSharedFlow<TodayWeatherContract.Effect>()
    val effect: SharedFlow<TodayWeatherContract.Effect> = _effect.asSharedFlow()

    init { // 一開始收到初始值(台北) 就會刷新UI
        viewModelScope.launch {
            selectedCityRepository.selectedCity.collect { city ->
                loadWeather(city)
            }
        }
    }

    // UI 來的 intent
    fun processIntent(intent: TodayWeatherContract.Intent) {
        when (intent) {
            is TodayWeatherContract.Intent.Refresh -> {
                val city = _state.value.selectedCity ?: return
                loadWeather(city)
            }
        }
    }

    private fun loadWeather(city: City) {
        viewModelScope.launch {
            // update{}：原子性地更新 StateFlow，Compose 收到後重新繪製
            _state.update { it.copy(isLoading = true, error = null, selectedCity = city) }

            // Loading中顯示，直到抓到以後就會回應
            when (val result = getCurrentWeatherUseCase(city)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, currentWeather = result.data) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    _effect.emit(TodayWeatherContract.Effect.ShowError(result.message))
                }
            }
        }
    }
}
