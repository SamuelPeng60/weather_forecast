package com.weather.feature.city

import com.weather.core.domain.model.City

//  選城市後 -> 更新 state -> 其他頁面也會刷新

object CityListContract {

    data class State(
        val cities: List<City> = emptyList(),
        val selectedCity: City? = null,
        // Map <國家名, 城市清單>
        val groupedCities: Map<String, List<City>> = emptyMap()
    )

    sealed class Intent {
        data class SelectCity(val city: City) : Intent()
    }

    sealed class Effect {
        data class CitySelected(val city: City) : Effect()
    }
}
