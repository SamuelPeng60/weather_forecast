package com.weather.feature.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.core.domain.model.CurrentWeather
import com.weather.core.ui.component.ErrorScreen
import com.weather.core.ui.component.LoadingScreen

// ════════════════════════════════════════════════════════
//  Feature Module — feature-today
//  Jetpack Compose UI 層
//
//  Compose 的核心概念：
//  - 用「描述」取代「命令」：你告訴 Compose 畫面長什麼樣，
//    它自動知道什麼時候要更新哪些部分
//  - 函式加上 @Composable 就能變成 UI 元件
//  - State 改變 → Compose 自動重新執行受影響的 @Composable
// ════════════════════════════════════════════════════════

// [Jetpack Compose] @Composable：
// 標記這個函式是 Compose UI 元件，只能在其他 @Composable 內呼叫
@Composable
fun TodayWeatherScreen(
    // [Jetpack Compose + Hilt] hiltViewModel()：
    // 自動建立並注入 ViewModel，與當前頁面生命週期綁定
    viewModel: TodayWeatherViewModel = hiltViewModel()
) {
    // [Coroutines + Compose] collectAsStateWithLifecycle：
    // 把 StateFlow 轉成 Compose 的 State
    // 頁面不可見時自動暫停收集，省電省資源
    // `by` 是委託語法，state 會自動解包 State 物件
    val state by viewModel.state.collectAsStateWithLifecycle()

    // [Jetpack Compose] LaunchedEffect：
    // 在 Composable 中安全地啟動協程
    // key = Unit 代表只在這個元件「第一次顯示」時執行一次
    LaunchedEffect(Unit) {
        // 持續收集一次性事件（Effect），例如顯示錯誤 Toast
        viewModel.effect.collect { effect ->
            when (effect) {
                is TodayWeatherContract.Effect.ShowError -> { /* 可在這裡顯示 Snackbar */ }
            }
        }
    }

    // [Jetpack Compose] 根據 state 決定顯示哪個畫面
    // 這就是「狀態驅動 UI」：state 是什麼，畫面就長什麼樣
    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(
            message = state.error!!,
            onRetry = { viewModel.processIntent(TodayWeatherContract.Intent.Refresh) }
        )
        state.currentWeather != null -> TodayWeatherContent(
            weather = state.currentWeather!!,
            onRefresh = { viewModel.processIntent(TodayWeatherContract.Intent.Refresh) }
        )
    }
}

// [Jetpack Compose] private fun：這個 Composable 只在這個檔案內使用
// 把複雜的 UI 拆成小函式，每個函式只負責一件事
@Composable
private fun TodayWeatherContent(
    weather: CurrentWeather,
    onRefresh: () -> Unit
) {
    val gradientColors = listOf(
        Color(0xFF1565C0),
        Color(0xFF42A5F5),
        Color(0xFF90CAF9)
    )

    // [Jetpack Compose] Box：讓子元素可以疊在一起（類似 FrameLayout）
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Modifier：修飾元件的外觀和行為，可以連續串接
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // [Jetpack Compose] Column：垂直排列子元件（類似 LinearLayout vertical）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 讓內容可以垂直捲動
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.cityName,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp)) // 空白間距

            Text(
                text = weather.description,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = weather.emoji,
                fontSize = 100.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "${weather.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "體感 ${weather.feelsLike.toInt()}°C",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(40.dp))

            // [Jetpack Compose] Card：Material3 的卡片元件
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                // [Jetpack Compose] Row：水平排列子元件（類似 LinearLayout horizontal）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItem(icon = Icons.Default.WaterDrop, label = "濕度", value = "${weather.humidity}%")
                    Divider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.height(60.dp).width(1.dp))
                    WeatherDetailItem(icon = Icons.Default.Air, label = "風速", value = "${weather.windSpeed} km/h")
                    Divider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.height(60.dp).width(1.dp))
                    WeatherDetailItem(icon = Icons.Default.Thermostat, label = "體感", value = "${weather.feelsLike.toInt()}°C")
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "更新時間：${weather.updateTime}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRefresh,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) {
                Text("重新整理")
            }
        }
    }
}

// [Jetpack Compose] 可重複使用的小元件
// 把「圖示 + 數值 + 標籤」這個組合抽出來，避免重複程式碼
@Composable
private fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
    }
}
