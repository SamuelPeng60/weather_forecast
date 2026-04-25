package com.weather.core.data.repository

import com.weather.core.domain.model.Cities
import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// 整個 App 共用一個目前城市
@Singleton
class SelectedCityRepositoryImpl @Inject constructor() : SelectedCityRepository {

    // Cities.defaultCity 初始值:台北
    private val _selectedCity = MutableStateFlow(Cities.defaultCity)

    // asStateFlow()：外部使用時 read only
    override val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    override fun selectCity(city: City) {
        // 更新 StateFlow 的值 有 collect 的會立即收到通知
        _selectedCity.value = city
    }
}
