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

//  使用者在這裡點選城市後，透過 SelectedCityRepository 的 StateFlow
//  自動通知今日天氣和週預報頁面刷新，不需要任何直接呼叫。
@Composable
fun CityListScreen(
    viewModel: CityListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { /* 城市切換由底部導覽列處理，不需要額外動作 */ }
    }

    // 漸層背景
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))
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
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "選擇城市",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            // state.selectedCity 來自 StateFlow，城市切換後這裡自動更新
            Text(
                text = "目前：${state.selectedCity?.nameZh ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 佔滿剩餘高度
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 依國家分組顯示：先顯示國家標題，再列出該國城市
            state.groupedCities.forEach { (country, cities) ->

                // [Jetpack Compose] item{}：LazyColumn 中的單一項目
                item {
                    Text(
                        text = country,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                    )
                }

                // items(list)：把 List 的每個元素都渲染成一個項目
                items(cities) { city ->
                    CityItem(
                        city = city,
                        // 比對 id 判斷是否為目前選中的城市（顯示 CheckCircle）
                        isSelected = city.id == state.selectedCity?.id,
                        onClick = {
                            // 把使用者動作包成 Intent 送給 ViewModel 處理（MVI 模式）
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
    isSelected: Boolean,  // 是否為目前選中的城市
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),  // 整張卡片都可以點擊
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            // 三元運算子：選中時較亮，未選中時較暗
            containerColor = if (isSelected) Color.White.copy(alpha = 0.28f)
                             else Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側圓形圖示
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

            // 城市名稱 + 經緯度（weight(1f) 讓這欄佔滿中間剩餘空間）
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

            // 選中時才顯示 CheckCircle 圖示
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

// ex：25.04779.format() -> "25.05"
private fun Double.format(): String = String.format("%.2f", this)
