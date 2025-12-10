package com.gujaraticalendar

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class CalendarWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_simple1)
        
        // 1. CSVમાંથી તિથિ, તહેવાર, સૂર્યોદય-સૂર્યાસ્ત
        val (tithiText, festival, sunrise, sunset) = getTodayDataFromCSV(context)
        views.setTextViewText(R.id.widget_month_tithi, tithiText)
        
        // 2. વાર
        val todayDay = getGujaratiDay()
        views.setTextViewText(R.id.widget_day, todayDay)
        
        // 3. રાશિ
        val rashi = getTodayRashi()
        views.setTextViewText(R.id.widget_rashi, "⭐ $rashi")
        
        // 4. ચોઘડિયુ (સૂર્યોદય-સૂર્યાસ્ત મુજબ)
        val choghadiya = calculateChoghadiyaFromSunriseSunset(sunrise, sunset)
        views.setTextViewText(R.id.widget_choghadiya, choghadiya)
        
        // 5. તહેવાર (જો હોય તો)
        if (festival.isNotEmpty()) {
            views.setViewVisibility(R.id.festival_container, android.view.View.VISIBLE)
            views.setTextViewText(R.id.widget_festival, festival)
            
            val icon = when {
                festival.contains("અગિયારસ") -> "🕉️"
                festival.contains("પૂનમ") -> "🌕"
                festival.contains("અમાસ") -> "🌑"
                festival.contains("જન્મદિવસ") -> "🎂"
                else -> "🎉"
            }
            views.setTextViewText(R.id.icon_festival, icon)
        } else {
            views.setViewVisibility(R.id.festival_container, android.view.View.GONE)
        }
        
        appWidgetManager.updateAppWidget(widgetId, views)
    }
    
    // CSVમાંથી આજનો ડેટા (તિથિ, તહેવાર, સૂર્યોદય, સૂર્યાસ્ત)
    private fun getTodayDataFromCSV(context: Context): TodayData {
        try {
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(Calendar.getInstance().time)
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line?.split(",")
                if (parts != null && parts.size >= 6) {
                    val date = parts[0].trim()
                    val month = parts[1].trim()
                    val tithi = parts[2].trim()
                    val festival = parts[3].trim()
                    val sunrise = parts[5].trim()  // SUNRISE TIME
                    val sunset = parts[6].trim()   // SUNSET TIME
                    
                    if (date == today && sunrise.isNotEmpty() && sunset.isNotEmpty()) {
                        reader.close()
                        val tithiText = "$month $tithi"
                        return TodayData(tithiText, festival, sunrise, sunset)
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // ડિફૉલ્ટ મૂલ્યો (લીંબડી ગામ માટે)
        return TodayData("માગશર વદ-૩", "", "07:24:00", "18:17:00")
    }
    
    // ગુજરાતી વાર
    private fun getGujaratiDay(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val gujaratiDays = arrayOf(
            "રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર",
            "ગુરુવાર", "શુક્રવાર", "શનિવાર"
        )
        return gujaratiDays[dayOfWeek - 1]
    }
    
    // રાશિ ગણતરી
    private fun getTodayRashi(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        return when (month) {
            1 -> if (day <= 19) "ધનુ" else "મકર"
            2 -> if (day <= 18) "મકર" else "કુંભ"
            3 -> if (day <= 20) "કુંભ" else "મીન"
            4 -> if (day <= 19) "મીન" else "મેષ"
            5 -> if (day <= 20) "મેષ" else "વૃષભ"
            6 -> if (day <= 21) "વૃષભ" else "મિથુન"
            7 -> if (day <= 22) "મિથુન" else "કર્ક"
            8 -> if (day <= 22) "કર્ક" else "સિંહ"
            9 -> if (day <= 22) "સિંહ" else "કન્યા"
            10 -> if (day <= 22) "કન્યા" else "તુલા"
            11 -> if (day <= 21) "તુલા" else "વૃશ્ચિક"
            12 -> if (day <= 21) "વૃશ્ચિક" else "ધનુ"
            else -> "મેષ"
        }
    }
    
    // સૂર્યોદય-સૂર્યાસ્ત મુજબ ચોઘડિયુ
    private fun calculateChoghadiyaFromSunriseSunset(
        sunriseStr: String, 
        sunsetStr: String
    ): String {
        try {
            // સમય ફોર્મેટ: "07:24:00"
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val sunrise = sdf.parse(sunriseStr)
            val sunset = sdf.parse(sunsetStr)
            val now = Calendar.getInstance().time
            
            if (sunrise != null && sunset != null) {
                // સૂર્યોદય-સૂર્યાસ્ત વચ્ચેનો સમય (મિલીસેકન્ડમાં)
                val dayDuration = sunset.time - sunrise.time
                
                // 8 ચોઘડિયુમાં વહેંચો
                val choghadiyaDuration = dayDuration / 8
                
                // વર્તમાન સમય કયા ચોઘડિયુમાં છે
                val currentTime = now.time
                
                for (i in 0..7) {
                    val choghadiyaStart = sunrise.time + (choghadiyaDuration * i)
                    val choghadiyaEnd = choghadiyaStart + choghadiyaDuration
                    
                    if (currentTime >= choghadiyaStart && currentTime < choghadiyaEnd) {
                        val choghadiyaList = arrayOf(
                            "અમૃત", "ચલ", "લાભ", "શુભ", 
                            "રોગ", "કાલ", "ઉદ્વેગ", "લાભ"
                        )
                        return choghadiyaList[i]
                    }
                }
                
                // રાત્રિનું ચોઘડિયુ (સૂર્યાસ્ત પછી)
                return "રાત્રિ"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // ડિફૉલ્ટ
        return "અમૃત"
    }
    
    // ડેટા ક્લાસ
    data class TodayData(
        val tithi: String,
        val festival: String,
        val sunrise: String,
        val sunset: String
    )
}
