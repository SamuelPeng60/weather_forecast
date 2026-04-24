package com.weather.core.domain.model

data class City(
    val id: String,
    val nameZh: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val country: String
)

object Cities {
    val all = listOf(
        City("taipei",    "台北",   25.0478,  121.5319, "Asia/Taipei",     "台灣"),
        City("taichung",  "台中",   24.1477,  120.6736, "Asia/Taipei",     "台灣"),
        City("kaohsiung", "高雄",   22.6273,  120.3014, "Asia/Taipei",     "台灣"),
        City("tokyo",     "東京",   35.6762,  139.6503, "Asia/Tokyo",      "日本"),
        City("osaka",     "大阪",   34.6937,  135.5023, "Asia/Tokyo",      "日本"),
        City("seoul",     "首爾",   37.5665,  126.9780, "Asia/Seoul",      "韓國"),
        City("beijing",   "北京",   39.9042,  116.4074, "Asia/Shanghai",   "中國"),
        City("shanghai",  "上海",   31.2304,  121.4737, "Asia/Shanghai",   "中國"),
        City("hongkong",  "香港",   22.3193,  114.1694, "Asia/Hong_Kong",  "香港"),
        City("singapore", "新加坡",  1.3521,  103.8198, "Asia/Singapore",  "新加坡"),
        City("bangkok",   "曼谷",   13.7563,  100.5018, "Asia/Bangkok",    "泰國"),
        City("london",    "倫敦",   51.5074,   -0.1278, "Europe/London",   "英國"),
        City("paris",     "巴黎",   48.8566,    2.3522, "Europe/Paris",    "法國"),
        City("newyork",   "紐約",   40.7128,  -74.0060, "America/New_York","美國"),
        City("losangeles","洛杉磯", 34.0522, -118.2437, "America/Los_Angeles","美國"),
        City("sydney",    "雪梨",  -33.8688,  151.2093, "Australia/Sydney","澳洲"),
        City("dubai",     "杜拜",   25.2048,   55.2708, "Asia/Dubai",      "阿聯酋"),
    )

    val defaultCity = all.first()
}
