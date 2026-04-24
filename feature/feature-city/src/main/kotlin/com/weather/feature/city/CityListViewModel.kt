package com.weather.feature.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.core.domain.repository.SelectedCityRepository
import com.weather.core.domain.usecase.GetCitiesUseCase
import com.weather.core.domain.usecase.SelectCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Feature Module — feature-city
//  城市列表 ViewModel
//
//  這個 ViewModel 做兩件事：
//  1. 載入城市清單並依國家分組
//  2. 選擇城市時更新 SelectedCityRepository 的 StateFlow，
//     讓 TodayWeather 和 WeeklyForecast 自動收到通知並刷新
// ════════════════════════════════════════════════════════

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val selectCityUseCase: SelectCityUseCase,
    // 直接注入 Repository 是為了訂閱「目前選中城市」來顯示 CheckCircle 標記
    private val selectedCityRepository: SelectedCityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CityListContract.State())
    val state: StateFlow<CityListContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CityListContract.Effect>()
    val effect: SharedFlow<CityListContract.Effect> = _effect.asSharedFlow()

    init {
        // 城市清單是靜態資料，不需要協程，直接取得
        val cities = getCitiesUseCase()
        // groupBy：Kotlin 集合函式，按照 country 欄位分組
        // 結果例如：{"台灣": [台北, 台中, 高雄], "日本": [東京, 大阪]}
        val grouped = cities.groupBy { it.country }

        // [Coroutines] 訂閱目前選中的城市（用來顯示 CheckCircle 標記）
        viewModelScope.launch {
            selectedCityRepository.selectedCity.collect { city ->
                _state.update {
                    it.copy(
                        cities = cities,
                        groupedCities = grouped,
                        selectedCity = city  // 更新選中城市，UI 會重新標記 CheckCircle
                    )
                }
            }
        }
    }

    fun processIntent(intent: CityListContract.Intent) {
        when (intent) {
            is CityListContract.Intent.SelectCity -> {
                // [Coroutines — StateFlow] 這一行是整個城市切換的核心！
                // 呼叫後，SelectedCityRepository 的 StateFlow 值更新，
                // TodayWeatherViewModel 和 WeeklyForecastViewModel 的 collect{} 都會觸發，
                // 自動重新載入新城市的天氣資料
                selectCityUseCase(intent.city)

                // [Coroutines] 發送一次性 Effect 通知 UI（城市已選擇）
                viewModelScope.launch {
                    _effect.emit(CityListContract.Effect.CitySelected(intent.city))
                }
            }
        }
    }
}
