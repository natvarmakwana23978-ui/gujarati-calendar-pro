// CSVમાંથી આજનો ડેટા મેળવવો
private fun getTodayDataFromCSV(context: Context): TodayData {
    try {
        val inputStream = context.assets.open("calendar_data.csv")
        val reader = inputStream.bufferedReader()
        
        // આજની તારીખ (2025/12/10)
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            .format(Calendar.getInstance().time)
        
        // ડીબગ માટે
        println("=== CSV DEBUG ===")
        println("Looking for date: $today")
        
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line?.split(",")
            if (parts != null && parts.size >= 7) {
                val date = parts[0].trim()
                
                if (date == today) {
                    val month = parts[1].trim()
                    val tithi = parts[2].trim()
                    val festival = parts[3].trim()
                    val sunrise = parts[5].trim()
                    val sunset = parts[6].trim()
                    
                    println("FOUND DATE: $date")
                    println("Month: $month, Tithi: $tithi")
                    println("Sunrise: $sunrise, Sunset: $sunset")
                    println("Festival: $festival")
                    
                    reader.close()
                    val tithiText = "$month $tithi"
                    return TodayData(tithiText, festival, sunrise, sunset)
                }
            }
        }
        reader.close()
        println("DATE NOT FOUND IN CSV")
    } catch (e: Exception) {
        println("CSV ERROR: ${e.message}")
        e.printStackTrace()
    }
    
    // ડિફૉલ્ટ
    return TodayData("માગશર વદ-૬", "", "07:11:00", "17:59:00")
}

// ચોઘડિયુ ગણતરી (સાચી)
private fun calculateChoghadiyaFromSunriseSunset(
    sunriseStr: String, 
    sunsetStr: String,
    dayOfWeek: Int
): String {
    println("=== CHOGHADIYA CALCULATION ===")
    println("Input: Sunrise=$sunriseStr, Sunset=$sunsetStr, Day=$dayOfWeek")
    
    try {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val sunrise = sdf.parse(sunriseStr)
        val sunset = sdf.parse(sunsetStr)
        
        val now = Calendar.getInstance()
        val currentTime = now.time
        
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        println("Current time: ${String.format("%02d:%02d:00", currentHour, currentMinute)}")
        
        if (sunrise != null && sunset != null) {
            val currentMillis = currentTime.time
            val sunriseMillis = sunrise.time
            val sunsetMillis = sunset.time
            
            // દિવસનો સમયગાળો (મિલિસેકન્ડમાં)
            val dayDuration = sunsetMillis - sunriseMillis
            val dayChoghadiyaDuration = dayDuration / 8
            
            // રાત્રિનો સમયગાળો
            val nightDuration = (24 * 60 * 60 * 1000) - dayDuration
            val nightChoghadiyaDuration = nightDuration / 8
            
            println("Day duration: ${dayDuration/60000} min")
            println("Night duration: ${nightDuration/60000} min")
            println("Day choghadiya: ${dayChoghadiyaDuration/60000} min each")
            println("Night choghadiya: ${nightChoghadiyaDuration/60000} min each")
            
            val isDaytime = currentMillis >= sunriseMillis && currentMillis < sunsetMillis
            println("Is daytime? $isDaytime")
            
            if (isDaytime) {
                // દિવસનું ચોઘડિયુ
                val timeSinceSunrise = currentMillis - sunriseMillis
                val choghadiyaIndex = (timeSinceSunrise / dayChoghadiyaDuration).toInt()
                
                println("Time since sunrise: ${timeSinceSunrise/60000} min")
                println("Choghadiya index: $choghadiyaIndex")
                
                val result = DAY_CHOGHADIYA[dayOfWeek]?.getOrElse(choghadiyaIndex) { "અમૃત" }
                println("RESULT: $result")
                return result
            } else {
                // રાત્રિનું ચોઘડિયુ
                val timeSinceSunset = if (currentMillis >= sunsetMillis) {
                    currentMillis - sunsetMillis
                } else {
                    // રાત્રિ 12 AM પછી
                    (24 * 60 * 60 * 1000) - sunsetMillis + currentMillis
                }
                
                val choghadiyaIndex = (timeSinceSunset / nightChoghadiyaDuration).toInt()
                
                println("Time since sunset: ${timeSinceSunset/60000} min")
                println("Choghadiya index: $choghadiyaIndex")
                println("Night choghadiya list: ${NIGHT_CHOGHADIYA[dayOfWeek]?.joinToString()}")
                
                val result = NIGHT_CHOGHADIYA[dayOfWeek]?.getOrElse(choghadiyaIndex) { "અમૃત" }
                println("RESULT: $result")
                return result
            }
        } else {
            println("ERROR: Could not parse sunrise/sunset times")
        }
    } catch (e: Exception) {
        println("CALCULATION ERROR: ${e.message}")
        e.printStackTrace()
    }
    
    return "અમૃત"
}
