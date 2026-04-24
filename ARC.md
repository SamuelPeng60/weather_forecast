# WeatherApp 架構文件

## 架構模式

- **整體架構**：Clean Architecture（三層分離）
- **UI 模式**：MVI（Model-View-Intent）
- **DI 框架**：Hilt
- **非同步**：Kotlin Coroutines + StateFlow / SharedFlow

---

## 模組結構

```
WeatherApp/
├── app/                    ← 入口、Navigation、Hilt 初始化
│
├── core/
│   ├── core-common/        ← 基礎工具（Result<T>）
│   ├── core-network/       ← Retrofit、Open-Meteo API、DTOs、NetworkModule
│   ├── core-domain/        ← Entity、UseCase、Repository Interface
│   ├── core-data/          ← Repository 實作、Mapper、DataModule
│   └── core-ui/            ← Compose 共用元件、WeatherTheme、Typography
│
└── feature/
    ├── feature-today/      ← 今日天氣頁（MVI）
    ├── feature-weekly/     ← 一週預報頁（MVI）
    └── feature-city/       ← 城市列表頁（MVI）
```

---

## 模組依賴圖

```
         ┌─────────────────────────────────┐
         │              app                │
         │   (Navigation + Hilt Setup)     │
         └────┬──────────┬──────────┬──────┘
              │          │          │
    ┌─────────▼──┐ ┌─────▼──────┐ ┌▼────────────┐
    │feature-today│ │feature-week│ │ feature-city│
    │  Screen     │ │ly  Screen  │ │  Screen     │
    │  ViewModel  │ │   ViewModel│ │  ViewModel  │
    │  (MVI)      │ │   (MVI)    │ │  (MVI)      │
    └──────┬──────┘ └─────┬──────┘ └──────┬──────┘
           │              │               │
           └──────────────▼───────────────┘
                    ┌────────────┐
                    │core-domain │
                    │  Entity    │
                    │  UseCase   │
                    │  Repo I/F  │
                    └─────┬──────┘
                          │
             ┌────────────┼────────────┐
             │            │            │
      ┌──────▼─────┐ ┌────▼──────┐ ┌──▼────────┐
      │ core-data  │ │core-network│ │  core-ui  │
      │ RepoImpl   │ │ ApiService │ │ Components│
      │ Mapper     │ │ DTOs       │ │ Theme     │
      │ DataModule │ │ NetworkMod │ └───────────┘
      └────────────┘ └────────────┘
             │              │
             └──────┬───────┘
                ┌───▼──────┐
                │core-common│
                │ Result<T> │
                └───────────┘
```

---

## Clean Architecture 三層對應

| 層次 | 位置 | 職責 |
|------|------|------|
| Presentation | feature-* 模組 | Screen（Compose UI）+ ViewModel（MVI） |
| Domain | core-domain | Entity、UseCase、Repository Interface（純 Kotlin） |
| Data | core-data + core-network | Repository 實作、DTO、Mapper、API 呼叫 |

---

## MVI 模式結構

每個 feature 模組都遵循以下三元組：

```kotlin
object XxxContract {
    data class State(...)          // 唯一不可變 UI 狀態
    sealed class Intent { ... }    // 使用者行為
    sealed class Effect { ... }    // 一次性事件（Toast、Navigation）
}

@HiltViewModel
class XxxViewModel : ViewModel() {
    val state: StateFlow<State>    // UI 訂閱
    val effect: SharedFlow<Effect> // 一次性事件
    fun processIntent(intent: Intent)
}
```

---

## 城市共享機制

`SelectedCityRepository` 以 `@Singleton` Hilt scope 注入，內部使用 `MutableStateFlow<City>`。

- `feature-city` 呼叫 `SelectCityUseCase` 更新城市
- `feature-today` 和 `feature-weekly` 的 ViewModel 在 `init` 區塊 `collect` 此 Flow，城市切換後自動重新載入天氣

```
CityListViewModel
    └─ SelectCityUseCase → SelectedCityRepository.selectCity(city)
                                    │ StateFlow 更新
                    ┌───────────────┴───────────────┐
          TodayWeatherViewModel            WeeklyForecastViewModel
          collect → loadWeather()          collect → loadForecast()
```

---

## API 設計（Open-Meteo）

**Base URL**：`https://api.open-meteo.com/`

```
GET /v1/forecast
  ?latitude={lat}
  &longitude={lon}
  &current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code
  &daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum
  &timezone={timezone}
  &forecast_days=7
```

- 免費使用，無需 API Key
- 一次呼叫同時取得「今日天氣」與「7 天預報」

---

## 技術版本

| 技術 | 版本 |
|------|------|
| AGP | 8.5.0 |
| Kotlin | 1.9.24 |
| Compose BOM | 2024.06.00 |
| Hilt | 2.51.1 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Coroutines | 1.8.1 |
| Navigation Compose | 2.7.7 |
| minSdk | 24 |
| targetSdk | 34 |
