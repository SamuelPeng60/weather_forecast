package com.weather.core.domain.usecase

import com.weather.core.domain.model.City
import com.weather.core.domain.repository.SelectedCityRepository
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Clean Architecture — Use Case（Domain 層）
//  業務動作：「切換城市」
//
//  呼叫後，SelectedCityRepository 內的 StateFlow 會更新，
//  所有訂閱它的 ViewModel 都會自動收到新城市並重新載入天氣。
// ════════════════════════════════════════════════════════

class SelectCityUseCase @Inject constructor(
    private val selectedCityRepository: SelectedCityRepository
) {
    operator fun invoke(city: City) = selectedCityRepository.selectCity(city)
}
