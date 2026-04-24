package com.weather.core.domain.usecase

import com.weather.core.domain.model.Cities
import com.weather.core.domain.model.City
import javax.inject.Inject

// ════════════════════════════════════════════════════════
//  Clean Architecture — Use Case（Domain 層）
//  業務動作：「取得城市清單」
//
//  這個 Use Case 不需要 suspend，因為資料是靜態的（Cities.all），
//  不需要呼叫網路或資料庫。
// ════════════════════════════════════════════════════════

class GetCitiesUseCase @Inject constructor() {
    operator fun invoke(): List<City> = Cities.all
}
