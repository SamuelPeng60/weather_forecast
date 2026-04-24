# WeatherApp — CLAUDE.md

## 專案概述

Android 天氣預報 App，採用 Clean Architecture + MVI + Jetpack Compose。
使用 Open-Meteo 免費 API（無需 API Key）。

---

## 檔案清單（50 個檔案）

### Gradle 設定

| 檔案 | 用途 |
|------|------|
| `settings.gradle.kts` | 模組註冊（9 個模組） |
| `build.gradle.kts` | 根 Gradle，宣告插件 |
| `gradle.properties` | JVM 參數、AndroidX 設定 |
| `gradle/libs.versions.toml` | 版本目錄（Version Catalog） |
| `app/build.gradle.kts` | App 模組依賴 |
| `core/core-common/build.gradle.kts` | 通用工具模組 |
| `core/core-domain/build.gradle.kts` | Domain 層（含 Hilt） |
| `core/core-network/build.gradle.kts` | 網路層（Retrofit + Hilt） |
| `core/core-data/build.gradle.kts` | 資料層（Hilt） |
| `core/core-ui/build.gradle.kts` | UI 共用（Compose） |
| `feature/feature-today/build.gradle.kts` | 今日天氣 Feature |
| `feature/feature-weekly/build.gradle.kts` | 週預報 Feature |
| `feature/feature-city/build.gradle.kts` | 城市列表 Feature |

### App 模組

| 檔案 | 用途 |
|------|------|
| `app/src/main/AndroidManifest.xml` | INTERNET 權限、Application/Activity 宣告 |
| `app/.../WeatherApplication.kt` | `@HiltAndroidApp` Application 類別 |
| `app/.../MainActivity.kt` | `@AndroidEntryPoint`，設定 Compose Content |
| `app/.../navigation/AppNavigation.kt` | BottomNavigationBar + NavHost（三頁路由） |

### core-common

| 檔案 | 用途 |
|------|------|
| `.../common/Result.kt` | `sealed class Result<T>`（Success / Error） |

### core-domain

| 檔案 | 用途 |
|------|------|
| `.../model/City.kt` | `City` data class + `Cities` 物件（17 個內建城市） |
| `.../model/CurrentWeather.kt` | 今日天氣 Entity |
| `.../model/DailyForecast.kt` | 單日預報 Entity |
| `.../repository/WeatherRepository.kt` | Repository 介面（getCurrentWeather / getWeeklyForecast） |
| `.../repository/SelectedCityRepository.kt` | 選擇城市 Repository 介面（StateFlow<City>） |
| `.../usecase/GetCurrentWeatherUseCase.kt` | 取得今日天氣 |
| `.../usecase/GetWeeklyForecastUseCase.kt` | 取得七天預報 |
| `.../usecase/GetCitiesUseCase.kt` | 取得城市清單 |
| `.../usecase/SelectCityUseCase.kt` | 切換城市 |

### core-network

| 檔案 | 用途 |
|------|------|
| `.../dto/WeatherResponseDto.kt` | Retrofit 回應 DTO（CurrentDto + DailyDto） |
| `.../api/WeatherApiService.kt` | Retrofit 介面，`GET /v1/forecast` |
| `.../di/NetworkModule.kt` | Hilt `@Module`，提供 OkHttpClient / Retrofit / ApiService |

### core-data

| 檔案 | 用途 |
|------|------|
| `.../mapper/WeatherMapper.kt` | DTO → Domain Entity 轉換 + 天氣代碼中文對照 |
| `.../repository/WeatherRepositoryImpl.kt` | `WeatherRepository` 實作 |
| `.../repository/SelectedCityRepositoryImpl.kt` | `SelectedCityRepository` 實作（Singleton MutableStateFlow） |
| `.../di/DataModule.kt` | Hilt `@Module`，`@Binds` 綁定兩個 Repository 介面 |

### core-ui

| 檔案 | 用途 |
|------|------|
| `.../theme/Color.kt` | 天藍色系 Color Palette |
| `.../theme/Theme.kt` | `WeatherTheme`（MaterialTheme wrapper） |
| `.../theme/Typography.kt` | 自訂 Typography 尺寸 |
| `.../component/CommonComponents.kt` | `LoadingScreen` + `ErrorScreen`（共用 Compose 元件） |

### feature-today

| 檔案 | 用途 |
|------|------|
| `.../today/TodayWeatherContract.kt` | MVI 三元組（State / Intent.Refresh / Effect.ShowError） |
| `.../today/TodayWeatherViewModel.kt` | 訂閱 selectedCity Flow，呼叫 GetCurrentWeatherUseCase |
| `.../today/TodayWeatherScreen.kt` | 漸層藍背景，大溫度字、濕度/風速/體感卡片 |

### feature-weekly

| 檔案 | 用途 |
|------|------|
| `.../weekly/WeeklyForecastContract.kt` | MVI 三元組（State / Intent.Refresh / Effect.ShowError） |
| `.../weekly/WeeklyForecastViewModel.kt` | 訂閱 selectedCity Flow，呼叫 GetWeeklyForecastUseCase |
| `.../weekly/WeeklyForecastScreen.kt` | LazyColumn 列出 7 天，今天標記 + 星期中文 |

### feature-city

| 檔案 | 用途 |
|------|------|
| `.../city/CityListContract.kt` | MVI 三元組（State / Intent.SelectCity / Effect.CitySelected） |
| `.../city/CityListViewModel.kt` | 取得城市清單、依國家分組、呼叫 SelectCityUseCase |
| `.../city/CityListScreen.kt` | 深藍背景，依國家分組顯示，選中城市有 CheckCircle 標記 |

---

## 內建城市清單（17 個）

| 城市 | 國家 |
|------|------|
| 台北、台中、高雄 | 台灣 |
| 東京、大阪 | 日本 |
| 首爾 | 韓國 |
| 北京、上海 | 中國 |
| 香港 | 香港 |
| 新加坡 | 新加坡 |
| 曼谷 | 泰國 |
| 倫敦 | 英國 |
| 巴黎 | 法國 |
| 紐約、洛杉磯 | 美國 |
| 雪梨 | 澳洲 |
| 杜拜 | 阿聯酋 |

---

## 導航結構

```
BottomNavigationBar
├── 今日天氣 → TodayWeatherScreen   (route: "today")
├── 週預報   → WeeklyForecastScreen  (route: "weekly")
└── 城市選擇 → CityListScreen        (route: "city")
```

切換城市後，今日天氣與週預報頁面透過 `SelectedCityRepository` 的 `StateFlow` 自動刷新。

---

## 開發注意事項

- `minSdk = 24`，避免使用 `java.time.*`，日期處理改用 `java.util.Calendar`
- `SelectedCityRepositoryImpl` 必須是 `@Singleton`，否則各頁面的城市狀態不同步
- 所有 ViewModel 使用 `@HiltViewModel + @Inject constructor`
- Library 模組需要最簡 `AndroidManifest.xml`（`<manifest />`）
- Open-Meteo API 回傳的 `weather_code` 對應中文說明在 `WeatherMapper.kt` 的 `toWeatherDescription()`

---

## 匯入與執行

1. Android Studio → File → Open → 選 `WeatherApp/` 資料夾
2. 等待 Gradle Sync 完成（自動下載依賴）
3. 選擇裝置或模擬器（API 24+）
4. Run ▶
