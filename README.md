# WeatherApp

A simple Android weather forecast app built with modern Android development practices.

## Features

- Current weather conditions (temperature, humidity, wind speed, feels like)
- 7-day weather forecast
- 17 built-in cities across Asia, Europe, Australia, and the Americas
- Automatic data refresh when switching cities

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | Clean Architecture + MVI + Feature Modules |
| Async | Kotlin Coroutines + StateFlow |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Weather API | [Open-Meteo](https://open-meteo.com/) (free, no API key required) |

## Project Structure

```
app/                  Entry point, navigation
core/
  core-common/        Shared utilities (Result)
  core-domain/        Business logic, models, use cases
  core-network/       Retrofit API, DTOs
  core-data/          Repository implementations, mappers
  core-ui/            Shared Compose components, theme
feature/
  feature-today/      Current weather screen
  feature-weekly/     7-day forecast screen
  feature-city/       City selection screen
```

## Getting Started

1. Clone the repo
2. Open in Android Studio
3. Wait for Gradle sync
4. Run on a device or emulator (API 24+)

No API key needed.

## Screenshots

| Today | Weekly | City |
|-------|--------|------|
| Current weather with gradient background | 7-day forecast list | Grouped city list with selection |
