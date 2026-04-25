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
        val cities = getCitiesUseCase()
        // ex: {"台灣": [台北, 台中, 高雄], "日本": [東京, 大阪] ...}
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
                // UseCase -> repository state flow update -> 另外兩頁也刷新
                selectCityUseCase(intent.city)

                // 通知UI
                viewModelScope.launch {
                    _effect.emit(CityListContract.Effect.CitySelected(intent.city))
                }
            }
        }
    }
}
