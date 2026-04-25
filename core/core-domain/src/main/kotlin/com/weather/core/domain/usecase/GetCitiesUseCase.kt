package com.weather.core.domain.usecase

import com.weather.core.domain.model.Cities
import com.weather.core.domain.model.City
import javax.inject.Inject

//  取得城市清單，因為是靜態的所以不需要suspend
class GetCitiesUseCase @Inject constructor() {
    operator fun invoke(): List<City> = Cities.all
}
