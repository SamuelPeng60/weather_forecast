package com.weather.feature.weekly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.core.domain.model.DailyForecast
import com.weather.core.ui.component.ErrorScreen
import com.weather.core.ui.component.LoadingScreen

// 週預報頁面的 Composable
@Composable
fun WeeklyForecastScreen(
    viewModel: WeeklyForecastViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingScreen()          // 載入中：轉圈動畫
        state.error != null -> ErrorScreen(         // 發生錯誤：顯示錯誤訊息 + 重試按鈕
            message = state.error!!,
            onRetry = { viewModel.processIntent(WeeklyForecastContract.Intent.Refresh) }
        )
        state.forecasts.isNotEmpty() -> WeeklyForecastContent(  // 成功：顯示七天預報
            cityName = state.selectedCity?.nameZh ?: "",
            forecasts = state.forecasts,
            onRefresh = { viewModel.processIntent(WeeklyForecastContract.Intent.Refresh) }
        )
    }
}

// [Jetpack Compose] 週預報內容，收到資料後才顯示
@Composable
private fun WeeklyForecastContent(
    cityName: String,
    forecasts: List<DailyForecast>,
    onRefresh: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF64B5F6))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // 頁面標題區
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "七天天氣預報",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // [Jetpack Compose] LazyColumn：高效能的可捲動列表
        // 只有畫面上看得到的卡片才會被渲染，節省記憶體
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 取 7 天, items()：Kotlin Lambda，對每個 DailyForecast 建立一張卡片
            items(forecasts.take(7)) { forecast ->
                ForecastDayCard(
                    forecast = forecast,
                    isToday = forecasts.indexOf(forecast) == 0  // 第一筆是今天
                )
            }
        }

        // 底部重新整理按鈕
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = onRefresh,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.6f))
            ) {
                Text("重新整理")
            }
        }
    }
}

// [Jetpack Compose] 單日預報卡片 Composable
// 今天和其他天的卡片外觀略有不同（背景透明度、文字粗細）
@Composable
private fun ForecastDayCard(
    forecast: DailyForecast,
    isToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // "今天" 的卡片背景較亮，其他天較暗
            containerColor = if (isToday) Color.White.copy(alpha = 0.3f)
                             else Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // [Jetpack Compose] Row：水平排列「日期 — 天氣 — 溫度」三個區塊
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：星期 / 今天 + 日期（固定寬度）
            Column(modifier = Modifier.width(56.dp)) {
                Text(
                    text = if (isToday) "今天" else forecast.dayOfWeek,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
                // substring(5)：從 "2024-04-21" 裁切出 "04-21"
                Text(
                    text = forecast.date.substring(5),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // 把中間和右側推開
            Spacer(Modifier.weight(1f))

            // 中間：天氣 emoji + 文字描述
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(2f)
            ) {
                Text(text = forecast.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = forecast.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(Modifier.weight(1f))

            // 右側：最高溫 / 最低溫
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${forecast.maxTemp.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecast.minTemp.toInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
