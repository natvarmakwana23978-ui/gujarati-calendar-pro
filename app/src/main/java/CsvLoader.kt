package com.gujaraticalendar

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class CsvLoader(private val context: Context) {
    
    data class PanchangData(
        val date: String,
        val month: String,
        val tithiName: String,
        val eventName: String,
        val sunrise: String,
        val sunset: String
    ) {
        val dayOfWeek: String
            get() = try {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
                val dateObj = sdf.parse(date)
                val calendar = Calendar.getInstance().apply { time = dateObj }
                
                val dayNames = arrayOf(
                    "રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર",
                    "ગુરુવાર", "શુક્રવાર", "શનિવાર"
                )
                dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            } catch (e: Exception) {
                // ભૂલ થાય તો આજનો વાર
                val calendar = Calendar.getInstance()
                val dayNames = arrayOf(
                    "રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર",
                    "ગુરુવાર", "શુક્રવાર", "શનિવાર"
                )
                dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            }
    }
    
    private val dayChoghadiyaPatterns = mapOf(
        "રવિવાર" to listOf("ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ"),
        "સોમવાર" to listOf("અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત"),
        "મંગળવાર" to listOf("રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ"),
        "બુધવાર" to listOf("લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ"),
        "ગુરુવાર" to listOf("શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ"),
        "શુક્રવાર" to listOf("ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ"),
        "શનિવાર" to listOf("કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ")
    )
    
    private val nightChoghadiyaPatterns = mapOf(
        "રવિવાર" to listOf("શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ"),
        "સોમવાર" to listOf("ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ"),
        "મંગળવાર" to listOf("કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ"),
        "બુધવાર" to listOf("ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ"),
        "ગુરુવાર" to listOf("અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત"),
        "શુક્રવાર" to listOf("રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ"),
        "શનિવાર" to listOf("લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ")
    )
    
    private fun loadPanchangData(): Map<String, PanchangData> {
        val panchangMap = mutableMapOf<String, PanchangData>()
        try {
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            
            // પહેલી લાઈન (હેડર) અવગણો
            reader.readLine()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                
                // ખાલી લાઈન અવગણો
                if (currentLine.trim().isEmpty() || currentLine.startsWith(",,,,,,")) {
                    continue
                }
                
                val columns = parseCsvLine(currentLine)
                
                // ઓછામાં ઓછી 7 કૉલમ હોવી જોઈએ
                if (columns.size >= 7 && columns[0].matches(Regex("^\\d{4}/\\d{2}/\\d{2}"))) {
                    // સૂર્યોદય/સૂર્યાસ્તમાંથી સેકન્ડ્સ કાઢો (06:47:00 -> 06:47)
                    val sunrise = columns[5].split(":").take(2).joinToString(":")
                    val sunset = columns[6].split(":").take(2).joinToString(":")
                    
                    val data = PanchangData(
                        date = columns[0].trim(),
                        month = columns[1].trim(),
                        tithiName = columns[2].trim(),
                        eventName = columns[3].trim(),
                        sunrise = sunrise,
                        sunset = sunset
                    )
                    panchangMap[data.date] = data
                }
            }
            
            reader.close()
            
        } catch (e: Exception) {
            Log.e("CSV_LOADER", "CSV ભૂલ", e)
        }
        return panchangMap
    }
    
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val parts = line.split(",")
        
        // પહેલી 7 કૉલમ લો
        for (i in 0..6) {
            if (i < parts.size) {
                result.add(parts[i].trim())
            } else {
                result.add("")
            }
        }
        return result
    }
    
    fun getTodayPanchang(): PanchangData? {
        // આજની તારીખ મેળવો
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        
        // CSVમાંથી ડેટા લોડ કરો
        val panchangMap = loadPanchangData()
        
        // DEBUG: કેટલી એન્ટ્રીઝ લોડ થઈ છે તે લોગ કરો
        Log.d("CSV_LOADER", "CSVમાં કુલ એન્ટ્રીઝ: ${panchangMap.size}")
        Log.d("CSV_LOADER", "આજની તારીખ શોધી રહ્યા છીએ: $today")
        
        // સીધી મેચ શોધો
        return panchangMap[today]
    }
    
    fun calculateChoghadiya(panchangData: PanchangData): String {
        try {
            val now = Calendar.getInstance()
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMin = now.get(Calendar.MINUTE)
            val currentTime = currentHour * 60 + currentMin
            
            val sunriseParts = panchangData.sunrise.split(":")
            val sunsetParts = panchangData.sunset.split(":")
            
            val sunriseMin = sunriseParts[0].toInt() * 60 + sunriseParts[1].toInt()
            val sunsetMin = sunsetParts[0].toInt() * 60 + sunsetParts[1].toInt()
            
            // DEBUG: સમયની માહિતી લોગ કરો
            Log.d("CHOGHADIYA", "સૂર્યોદય: ${panchangData.sunrise} ($sunriseMin મિનિટ)")
            Log.d("CHOGHADIYA", "સૂર્યાસ્ત: ${panchangData.sunset} ($sunsetMin મિનિટ)")
            Log.d("CHOGHADIYA", "વર્તમાન સમય: $currentHour:$currentMin ($currentTime મિનિટ)")
            Log.d("CHOGHADIYA", "વાર: ${panchangData.dayOfWeek}")
            
            if (currentTime >= sunriseMin && currentTime <= sunsetMin) {
                // દિવસનું ચોઘડિયું
                val dayDuration = sunsetMin - sunriseMin
                val dayChoghadiyaDuration = dayDuration / 8.0
                val elapsed = (currentTime - sunriseMin).toDouble()
                var choghadiyaIndex = (elapsed / dayChoghadiyaDuration).toInt()
                if (choghadiyaIndex >= 8) choghadiyaIndex = 7
                
                val pattern = dayChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                val result = pattern[choghadiyaIndex]
                
                Log.d("CHOGHADIYA", "દિવસનું ચોઘડિયું: ઇન્ડેક્સ=$choghadiyaIndex, પરિણામ=$result")
                return result
            } else {
                // રાત્રિનું ચોઘડિયું
                val nightDuration = if (currentTime > sunsetMin) {
                    24 * 60 - sunsetMin + sunriseMin
                } else {
                    (24 * 60 - sunsetMin) + currentTime
                }
                
                val nightChoghadiyaDuration = nightDuration / 8.0
                val nightTime = if (currentTime > sunsetMin) {
                    currentTime - sunsetMin
                } else {
                    (24 * 60 - sunsetMin) + currentTime
                }
                
                var choghadiyaIndex = (nightTime / nightChoghadiyaDuration).toInt()
                if (choghadiyaIndex >= 8) choghadiyaIndex = 7
                
                val pattern = nightChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                val result = pattern[choghadiyaIndex]
                
                Log.d("CHOGHADIYA", "રાત્રિનું ચોઘડિયું: ઇન્ડેક્સ=$choghadiyaIndex, પરિણામ=$result")
                return result
            }
        } catch (e: Exception) {
            Log.e("CHOGHADIYA", "ચોઘડિયું ગણતરી ભૂલ", e)
            return "કાળ"
        }
    }
    
    fun formatTithi(tithi: String): String {
        return try {
            if (tithi.startsWith("સુદ")) {
                if (tithi == "સુદ-૧૫") {
                    "સુદ પૂર્ણિમા"
                } else {
                    tithi.replace("સુદ-", "સુદ ")
                }
            } else if (tithi.startsWith("વદ")) {
                if (tithi == "વદ-૧૫") {
                    "વદ અમાસ"
                } else {
                    tithi.replace("વદ-", "વદ ")
                }
            } else {
                tithi
            }
        } catch (e: Exception) {
            tithi
        }
    }
    
    fun getVikramSamvat(): String {
        return "વિક્રમ સંવત - ૨૦૮૨"
    }
    
    fun getFirstAvailableDate(): String {
        return try {
            val panchangMap = loadPanchangData()
            if (panchangMap.isNotEmpty()) {
                panchangMap.keys.sorted().first()
            } else {
                SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
            }
        } catch (e: Exception) {
            SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        }
    }
}
