package com.weather.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weather.feature.city.CityListScreen
import com.weather.feature.today.TodayWeatherScreen
import com.weather.feature.weekly.WeeklyForecastScreen

//  這裡把三個  Module 的 Screen 組合在一起，加上底部導覽列切換
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Today  : Screen("today",  "今日天氣", Icons.Default.WbSunny)
    object Weekly : Screen("weekly", "週預報",   Icons.Default.Cloud)
    object City   : Screen("city",   "城市選擇", Icons.Default.LocationCity)
}

// 底部導覽列 今日 一週 城市選擇
private val bottomNavItems = listOf(Screen.Today, Screen.Weekly, Screen.City)

@Composable
fun AppNavigation() {

    // [Jetpack Compose Navigation] rememberNavController：
    // 建立並記住 NavController（頁面控制器），用來執行頁面切換
    val navController = rememberNavController()

    // 導覽列的目前頁面高亮
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Scaffold -> Material3 的頁面框架
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        // hierarchy：判斷目前路由是否符合這個項目
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // popUpTo：切換頁面時回到起點，避免頁面堆疊一直增長
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true   // 儲存該頁面的 scroll 位置等狀態
                                }
                                launchSingleTop = true  // 同一個頁面不重複疊加
                                restoreState = true     // 切換回來時恢復之前的狀態
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        // 根據目前的 route 決定顯示哪個 Composable
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,  // App 啟動時預設顯示今日天氣
            modifier = Modifier.padding(innerPadding) // 避免內容被底部導覽列蓋住
        ) {
            // composable()：註冊每個路由對應的 Composable
            // 每個 Screen 對應一個 Feature Module 的根 Composable
            composable(Screen.Today.route)  { TodayWeatherScreen() }
            composable(Screen.Weekly.route) { WeeklyForecastScreen() }
            composable(Screen.City.route)   { CityListScreen() }
        }
    }
}
