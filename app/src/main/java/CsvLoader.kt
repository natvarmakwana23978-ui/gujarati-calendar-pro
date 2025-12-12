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
        "સોમવાર" to listOf("અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત"),
        "મંગળવાર" to listOf("રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ"),
        "બુધવાર" to listOf("લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ"),
        "ગુરુવાર" to listOf("શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ"),
        "શુક્રવાર" to listOf("ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ"),
        "શનિવાર" to listOf("કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ"),
        "રવિવાર" to listOf("ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ")
    )
    
    private val nightChoghadiyaPatterns = mapOf(
        "સોમવાર" to listOf("ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ"),
        "મંગળવાર" to listOf("કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ"),
        "બુધવાર" to listOf("ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ"),
        "ગુરુવાર" to listOf("અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત"),
        "શુક્રવાર" to listOf("રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ"),
        "શનિવાર" to listOf("લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ"),
        "રવિવાર" to listOf("શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ")
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
            Log.i("CSV_LOADER", "કુલ લોડ: ${panchangMap.size} એન્ટ્રીઓ")
            
        } catch (e: Exception) {
            Log.e("CSV_LOADER", "CSV ભૂલ", e)
        }
        return panchangMap
    }
    
    private fun parseCsvLine(line: String): List<String> {
        if (line.contains("\"")) {
            return parseCsvWithQuotes(line)
        }
        return line.split(",").map { it.trim() }
    }
    
    private fun parseCsvWithQuotes(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var insideQuotes = false
        
        for (c in line) {
            when {
                c == '"' -> insideQuotes = !insideQuotes
                c == ',' && !insideQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(c)
            }
        }
        result.add(current.toString())
        return result
    }
    
    fun getTodayPanchang(): PanchangData? {
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        return loadPanchangData()[today]
    }
    
    fun getFirstAvailableDate(): PanchangData? {
        val allData = loadPanchangData()
        return if (allData.isNotEmpty()) allData[allData.keys.first()] else null
    }
    
    // ચોઘડિયા ગણતરી (સુધારેલી)
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
                val choghadiyaIndex = (elapsed / dayChoghadiyaDuration).toInt()
                val pattern = dayChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                return pattern[choghadiyaIndex.coerceIn(0, 7)]
            } else {
                val nightDuration = (24 * 60 - sunsetMin) + sunriseMin
                val nightChoghadiyaDuration = nightDuration / 8.0
                val nightTime = if (currentTime > sunsetMin) {
                    currentTime - sunsetMin
                } else {
                    (24 * 60 - sunsetMin) + currentTime
                }
                
                val choghadiyaIndex = (nightTime / nightChoghadiyaDuration).toInt()
                val pattern = nightChoghadiyaPatterns[panchangData.dayOfWeek] ?: return "કાળ"
                return pattern[choghadiyaIndex.coerceIn(0, 7)]
            }
        } catch (e: Exception) {
            return "કાળ"
        }
    }
    
    // તિથિ ફોર્મેટ (સુધારેલી - character literal ભૂલ દુર)
    fun formatTithi(tithi: String): String {
        return try {
            if (tithi.startsWith("સુદ")) {
                if (tithi == "સુદ-૧૫") {
                    "સુદ પૂનમ"
                } else {
                    val number = tithi.substringAfter("-")
                    val gujaratiNumber = when (number) {
                        "૧૦" -> "દશમ"
                        "૧૧" -> "અગિયારસ"
                        "૧૨" -> "બારસ"
                        "૧૩" -> "તેરસ"
                        "૧૪" -> "ચૌદસ"
                        else -> {
                            number.map { char ->
                                when (char) {
                                    '૦' -> "શૂન્ય"
                                    '૧' -> "એકમ"
                                    '૨' -> "બીજ"
                                    '૩' -> "ત્રીજ"
                                    '૪' -> "ચોથ"
                                    '૫' -> "પાંચમ"
                                    '૬' -> "છઠ્ઠ"
                                    '૭' -> "સાતમ"
                                    '૮' -> "આઠમ"
                                    '૯' -> "નોમ"
                                    else -> char.toString()
                                }
                            }.joinToString("")
                        }
                    }
                    "સુદ $gujaratiNumber"
                }
            } else if (tithi.startsWith("વદ")) {
                if (tithi == "વદ-૧૫") {
                    "વદ અમાસ"
                } else {
                    val number = tithi.substringAfter("-")
                    val gujaratiNumber = when (number) {
                        "૧૦" -> "દશમ"
                        "૧૧" -> "અગિયારસ"
                        "૧૨" -> "બારસ"
                        "૧૩" -> "તેરસ"
                        "૧૪" -> "ચૌદસ"
                        else -> {
                            number.map { char ->
                                when (char) {
                                    '૦' -> "શૂન્ય"
                                    '૧' -> "એકમ"
                                    '૨' -> "બીજ"
                                    '૩' -> "ત્રીજ"
                                    '૪' -> "ચોથ"
                                    '૫' -> "પાંચમ"
                                    '૬' -> "છઠ્ઠ"
                                    '૭' -> "સાતમ"
                                    '૮' -> "આઠમ"
                                    '૯' -> "નોમ"
                                    else -> char.toString()
                                }
                            }.joinToString("")
                        }
                    }
                    "વદ $gujaratiNumber"
                }
            } else {
                tithi
            }
        } catch (e: Exception) {
            tithi
        }
    }
    
    // વિક્રમ સંવત (લેબલ માત્ર)
    fun getVikramSamvat(): String {
        return "વિક્રમ સંવત - ૨૦૮૨"
    }
}
