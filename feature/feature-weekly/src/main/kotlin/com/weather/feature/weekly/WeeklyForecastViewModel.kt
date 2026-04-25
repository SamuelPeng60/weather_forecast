package com.weather.feature.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.core.common.Result
import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import com.weather.core.domain.usecase.GetWeeklyForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeeklyForecastViewModel @Inject constructor(
    private val getWeeklyForecastUseCase: GetWeeklyForecastUseCase,
    private val selectedCityRepository: SelectedCityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeeklyForecastContract.State())
    val state: StateFlow<WeeklyForecastContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WeeklyForecastContract.Effect>()
    val effect: SharedFlow<WeeklyForecastContract.Effect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            selectedCityRepository.selectedCity.collect { city ->
                loadForecast(city)
            }
        }
    }

    fun processIntent(intent: WeeklyForecastContract.Intent) {
        when (intent) {
            is WeeklyForecastContract.Intent.Refresh -> {
                val city = _state.value.selectedCity ?: return
                loadForecast(city)
            }
        }
    }

    private fun loadForecast(city: City) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, selectedCity = city) }
            when (val result = getWeeklyForecastUseCase(city)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, forecasts = result.data) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    _effect.emit(WeeklyForecastContract.Effect.ShowError(result.message))
                }
            }
        }
    }
}
