package com.gujaraticalendar

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class CalendarWidget : AppWidgetProvider() {
    
    // ડેટા ક્લાસ
    data class TodayData(
        val tithi: String,
        val festival: String,
        val sunrise: String,
        val sunset: String
    )
    
    // દિવસના ચોઘડિયા (1=રવિવાર, 2=સોમવાર, ..., 7=શનિવાર)
    private val DAY_CHOGHADIYA = mapOf(
        Calendar.SUNDAY to arrayOf("ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ"),
        Calendar.MONDAY to arrayOf("અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત"),
        Calendar.TUESDAY to arrayOf("રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ"),
        Calendar.WEDNESDAY to arrayOf("લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ"),
        Calendar.THURSDAY to arrayOf("શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ", "શુભ"),
        Calendar.FRIDAY to arrayOf("ચલ", "લાભ", "અમૃત", "કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ"),
        Calendar.SATURDAY to arrayOf("કાળ", "શુભ", "રોગ", "ઉદ્વેગ", "ચલ", "લાભ", "અમૃત", "કાળ")
    )
    
    // રાત્રિના ચોઘડિયા
    private val NIGHT_CHOGHADIYA = mapOf(
        Calendar.SUNDAY to arrayOf("શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ"),
        Calendar.MONDAY to arrayOf("ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ"),
        Calendar.TUESDAY to arrayOf("કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ"),
        Calendar.WEDNESDAY to arrayOf("ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ"),
        Calendar.THURSDAY to arrayOf("અમૃત", "ચલ", "રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત"),
        Calendar.FRIDAY to arrayOf("રોગ", "કાળ", "લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ"),
        Calendar.SATURDAY to arrayOf("લાભ", "ઉદ્વેગ", "શુભ", "અમૃત", "ચલ", "રોગ", "કાળ", "લાભ")
    )
    
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
        
        // 1. વિક્રમ સંવત
        views.setTextViewText(R.id.widget_vikram_samvat, "વિક્રમ સંવત ૨૦૮૨")
        
        // 2. CSVમાંથી તિથિ, તહેવાર, સૂર્યોદય-સૂર્યાસ્ત
        val (tithiText, festival, sunrise, sunset) = getTodayDataFromCSV(context)
        views.setTextViewText(R.id.widget_month_tithi, tithiText)
        
        // 3. વાર
        val todayDay = getGujaratiDay()
        views.setTextViewText(R.id.widget_day, todayDay)
        
        // 4. રાશિ
        val rashi = getTodayRashi()
        views.setTextViewText(R.id.widget_rashi, "⭐ $rashi")
        
        // 5. ચોઘડિયુ (CSVમાંથી સૂર્યોદય-સૂર્યાસ્ત મુજબ)
        val now = Calendar.getInstance()
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val choghadiya = calculateChoghadiyaFromSunriseSunset(sunrise, sunset, dayOfWeek)
        views.setTextViewText(R.id.widget_choghadiya, choghadiya)
        
        // 6. તહેવાર (જો હોય તો)
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
    
    // CSVમાંથી આજનો ડેટા મેળવવો
    private fun getTodayDataFromCSV(context: Context): TodayData {
        try {
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(Calendar.getInstance().time)
            
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
        
        // ડિફૉલ્ટ (10 Dec 2025 માટે)
        return TodayData("માગશર વદ-૬", "", "07:11:00", "17:59:00")
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
    
    // ચોઘડિયુ ગણતરી (સૂર્યોદય-સૂર્યાસ્ત મુજબ)
    private fun calculateChoghadiyaFromSunriseSunset(
        sunriseStr: String, 
        sunsetStr: String,
        dayOfWeek: Int
    ): String {
        try {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val sunrise = sdf.parse(sunriseStr)
            val sunset = sdf.parse(sunsetStr)
            val now = Calendar.getInstance()
            val currentTime = now.time
            
            if (sunrise != null && sunset != null) {
                val currentMillis = currentTime.time
                val sunriseMillis = sunrise.time
                val sunsetMillis = sunset.time
                
                // દિવસનો સમયગાળો
                val dayDuration = sunsetMillis - sunriseMillis
                val dayChoghadiyaDuration = dayDuration / 8
                
                // રાત્રિનો સમયગાળો
                val nightDuration = (24 * 60 * 60 * 1000) - dayDuration
                val nightChoghadiyaDuration = nightDuration / 8
                
                val isDaytime = currentMillis >= sunriseMillis && currentMillis < sunsetMillis
                
                if (isDaytime) {
                    // દિવસનું ચોઘડિયુ
                    val timeSinceSunrise = currentMillis - sunriseMillis
                    val choghadiyaIndex = (timeSinceSunrise / dayChoghadiyaDuration).toInt()
                    
                    if (choghadiyaIndex in 0..7) {
                        return DAY_CHOGHADIYA[dayOfWeek]?.get(choghadiyaIndex) ?: "અમૃત"
                    }
                } else {
                    // રાત્રિનું ચોઘડિયુ
                    val timeSinceSunset = if (currentMillis >= sunsetMillis) {
                        currentMillis - sunsetMillis
                    } else {
                        currentMillis + (24 * 60 * 60 * 1000) - sunsetMillis
                    }
                    
                    val choghadiyaIndex = (timeSinceSunset / nightChoghadiyaDuration).toInt()
                    
                    if (choghadiyaIndex in 0..7) {
                        return NIGHT_CHOGHADIYA[dayOfWeek]?.get(choghadiyaIndex) ?: "અમૃત"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return "અમૃત"
    }
}
