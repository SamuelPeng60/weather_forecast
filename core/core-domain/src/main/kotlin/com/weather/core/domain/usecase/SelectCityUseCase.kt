package com.weather.core.domain.usecase

import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import javax.inject.Inject

//  用途是切換城市
class SelectCityUseCase @Inject constructor(
    private val selectedCityRepository: SelectedCityRepository
) {
    operator fun invoke(city: City) = selectedCityRepository.selectCity(city)
}
