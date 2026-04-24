package com.weather.feature.city

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.core.domain.model.City

@Composable
fun CityListScreen(
    viewModel: CityListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { /* navigation handled by bottom nav */ }
    }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // 標題
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "選擇城市",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "目前：${state.selectedCity?.nameZh ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        // 城市列表（依國家分組）
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            state.groupedCities.forEach { (country, cities) ->
                item {
                    // 國家標題
                    Text(
                        text = country,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                    )
                }
                items(cities) { city ->
                    CityItem(
                        city = city,
                        isSelected = city.id == state.selectedCity?.id,
                        onClick = {
                            viewModel.processIntent(CityListContract.Intent.SelectCity(city))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityItem(
    city: City,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color.White.copy(alpha = 0.28f)
            else
                Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 城市圖示
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // 城市名稱
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.nameZh,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${city.latitude.format()}°N, ${city.longitude.format()}°E",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // 選中標記
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "已選擇",
                    tint = Color(0xFF80CBC4),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun Double.format(): String = String.format("%.2f", this)
