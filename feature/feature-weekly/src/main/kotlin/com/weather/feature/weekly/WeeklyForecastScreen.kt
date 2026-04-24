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

@Composable
fun WeeklyForecastScreen(
    viewModel: WeeklyForecastViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(
            message = state.error!!,
            onRetry = { viewModel.processIntent(WeeklyForecastContract.Intent.Refresh) }
        )
        state.forecasts.isNotEmpty() -> WeeklyForecastContent(
            cityName = state.selectedCity?.nameZh ?: "",
            forecasts = state.forecasts,
            onRefresh = { viewModel.processIntent(WeeklyForecastContract.Intent.Refresh) }
        )
    }
}

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
        // 標題區
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

        // 預報列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(forecasts.take(7)) { forecast ->
                ForecastDayCard(
                    forecast = forecast,
                    isToday = forecasts.indexOf(forecast) == 0
                )
            }
        }

        // 刷新按鈕
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

@Composable
private fun ForecastDayCard(
    forecast: DailyForecast,
    isToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday)
                Color.White.copy(alpha = 0.3f)
            else
                Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 星期 / 今天
            Column(modifier = Modifier.width(56.dp)) {
                Text(
                    text = if (isToday) "今天" else forecast.dayOfWeek,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = forecast.date.substring(5),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.weight(1f))

            // 天氣 emoji + 描述
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

            // 溫度範圍
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
