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
                "શુક્રવાર"
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
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                
                if (currentLine.matches(Regex("^\\d{4}/\\d{2}/\\d{2}.*"))) {
                    val columns = parseCsvLine(currentLine)
                    
                    if (columns.size >= 6) {
                        val data = PanchangData(
                            date = columns[0].trim(),
                            month = columns[1].trim(),
                            tithiName = columns[2].trim(),
                            eventName = columns[3].trim(),
                            sunrise = columns[4].trim(),
                            sunset = columns[5].trim()
                        )
                        panchangMap[data.date] = data
                    }
                }
            }
            
            reader.close()
            
        } catch (e: Exception) {
            Log.e("CSV_LOADER", "CSV ભૂલ", e)
        }
        return panchangMap
    }
    
    private fun parseCsvLine(line: String): List<String> {
        return line.split(",").map { it.trim() }
    }
    
    fun getTodayPanchang(): PanchangData? {
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        return loadPanchangData()[today]
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
            
            if (currentTime >= sunriseMin && currentTime <= sunsetMin) {
                val dayDuration = sunsetMin - sunriseMin
                val dayChoghadiyaDuration = dayDuration / 8.0
                val elapsed = (currentTime - sunriseMin).toDouble()
                var choghadiyaIndex = (elapsed / dayChoghadiyaDuration).toInt()
                if (choghadiyaIndex >= 8) choghadiyaIndex = 7
                val pattern = dayChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                return pattern[choghadiyaIndex]
            } else {
                val nightDuration = (24 * 60 - sunsetMin) + sunriseMin
                val nightChoghadiyaDuration = nightDuration / 8.0
                val nightTime = if (currentTime > sunsetMin) {
                    currentTime - sunsetMin
                } else {
                    (24 * 60 - sunsetMin) + currentTime
                }
                
                var choghadiyaIndex = (nightTime / nightChoghadiyaDuration).toInt()
                if (choghadiyaIndex >= 8) choghadiyaIndex = 7
                val pattern = nightChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                return pattern[choghadiyaIndex]
            }
        } catch (e: Exception) {
            return "કાળ"
        }
    }
    
    fun formatTithi(tithi: String): String {
        return try {
            if (tithi.startsWith("સુદ")) {
                if (tithi == "સુદ-૧૫") {
                    "સુદ પૂનમ"
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
            // પ્રથમ ઉપલબ્ધ તારીખ પાછી આપો
            panchangMap.keys.sorted().first()
        } else {
            // જો CSV ખાલી હોય તો આજની તારીખ
            SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        }
    } catch (e: Exception) {
        // ભૂલ થાય તો આજની તારીખ
        SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
    }
    }
}
