// CsvLoader.kt - સંપૂર્ણ સુધારેલી ફાઈલ
package com.gujaraticalendar  // ✅ તમારું સાચું પેકેજ નામ

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class CsvLoader(private val context: Context) {
    
    // CSV ડેટા માટે મોડેલ ક્લાસ
    data class SimplePanchangData(
        val date: String,              // "2025/10/22"
        val month: String,             // "કારતક"
        val tithiName: String,         // "સુદ-૧"
        val eventName: String,         // "બેસતુ વર્ષ" અથવા ""
        val eventType: String,         // "તહેવાર" અથવા ""
        val sunrise: String,           // "06:47:00"
        val sunset: String             // "18:05:00"
    )
    
    // CSV ફાઈલ લોડ કરવાની મુખ્ય ફંક્શન
    private fun loadPanchangData(): Map<String, SimplePanchangData> {
        val panchangMap = mutableMapOf<String, SimplePanchangData>()
        
        try {
            // 1. assets ફોલ્ડરમાંથી CSV ફાઈલ ખોલો
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            
            // 2. હેડર લાઇન (પહેલી લાઇન) છોડો
            reader.readLine()
            
            var lineCount = 0
            var line: String?
            
            // 3. દરેક લાઇન વાંચો
            while (reader.readLine().also { line = it } != null) {
                lineCount++
                val currentLine = line ?: continue
                
                // 4. CSV લાઇન પાર્સ કરો (કોમા સેપરેટેડ)
                val columns = parseCsvLine(currentLine)
                
                // 5. કૉલમ્સ ચેક કરો (ઓછામાં ઓછી 7 કૉલમ્સ હોવી જોઈએ)
                if (columns.size >= 7) {
                    val data = SimplePanchangData(
                        date = columns[0].trim(),
                        month = columns[1].trim(),
                        tithiName = columns[2].trim(),
                        eventName = columns[3].trim(),
                        eventType = columns[4].trim(),
                        sunrise = columns[5].trim(),
                        sunset = columns[6].trim()
                    )
                    
                    // 6. HashMap માં સંગ્રહ કરો (તારીખ → ડેટા)
                    panchangMap[data.date] = data
                    
                    // 7. પહેલી 2 લાઇન લોગ કરો (ડિબગ માટે)
                    if (lineCount <= 2) {
                        Log.d("CSV_LOADER", "લાઇન $lineCount: ${data.date} - ${data.tithiName}")
                    }
                } else {
                    Log.e("CSV_LOADER", "ખોટી લાઇન $lineCount (${columns.size} કૉલમ્સ): $currentLine")
                }
            }
            
            // 8. લોગ: કેટલી લાઇન્સ લોડ થઈ
            Log.i("CSV_LOADER", "કુલ લોડ: $lineCount લાઇન્સ, ${panchangMap.size} એન્ટ્રીઓ")
            
            // 9. રિસોર્સ બંધ કરો
            reader.close()
            inputStream.close()
            
        } catch (e: Exception) {
            Log.e("CSV_LOADER", "CSV લોડ કરતી વખતે ભૂલ", e)
        }
        
        return panchangMap
    }
    
    // CSV લાઇન પાર્સ કરવાની સરળ ફંક્શન
    private fun parseCsvLine(line: String): List<String> {
        // જો લાઇનમાં quotes હોય તો handle કરવા
        if (line.contains("\"")) {
            return parseCsvWithQuotes(line)
        }
        // સરળ કોમા સેપરેશન
        return line.split(",").map { it.trim() }
    }
    
    // Quotes સાથે CSV પાર્સ કરવા
    private fun parseCsvWithQuotes(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var insideQuotes = false
        
        for (i in line.indices) {
            val c = line[i]
            
            when {
                c == '"' -> {
                    insideQuotes = !insideQuotes
                }
                c == ',' && !insideQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> {
                    current.append(c)
                }
            }
        }
        
        result.add(current.toString())
        return result
    }
    
    // ✅ 1. આજની તારીખ માટે CSV ડેટા શોધો (MainActivity માં જોઈતું)
    fun getTodayPanchang(): SimplePanchangData? {
        // આજની તારીખ "YYYY/MM/DD" ફોર્મેટમાં મેળવો
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            .format(Date())
        
        Log.d("CSV_LOADER", "આજની તારીખ શોધી રહ્યા: $today")
        
        // CSV ડેટા લોડ કરો
        val allData = loadPanchangData()
        
        // આજની તારીખનો ડેટા શોધો
        val todayData = allData[today]
        
        if (todayData == null) {
            Log.w("CSV_LOADER", "આજની તારીખ મળી નથી: $today")
        }
        
        return todayData
    }
    
    // ✅ 2. MainActivity માં જોઈતું getFirstAvailableDate() ફંક્શન
    fun getFirstAvailableDate(): SimplePanchangData? {
        val allData = loadPanchangData()
        
        return if (allData.isNotEmpty()) {
            val firstDate = allData.keys.first()
            val firstData = allData[firstDate]
            Log.d("CSV_LOADER", "પહેલી ઉપલબ્ધ તારીખ: $firstDate")
            firstData
        } else {
            Log.e("CSV_LOADER", "કોઈ CSV ડેટા લોડ થયો નથી")
            null
        }
    }
    
    // ✅ 3. ટેસ્ટ માટે: ચોક્કસ તારીખ માટે ડેટા લાવો
    fun getPanchangForDate(date: String): SimplePanchangData? {
        return loadPanchangData()[date]
    }
    
    // ✅ 4. ડિફૉલ્ટ ડેટા (જો CSV ન મળે તો)
    private fun getDefaultPanchangData(): SimplePanchangData {
        return SimplePanchangData(
            date = "2024/12/01",
            month = "ચૈત્ર",
            tithiName = "પ્રતિપ્રદા",
            eventName = "",
            eventType = "",
            sunrise = "06:00:00",
            sunset = "18:00:00"
        )
    }
}
