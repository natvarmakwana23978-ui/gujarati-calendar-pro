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
        
        // 1. CSVમાંથી તિથિ અને તહેવાર
        val (tithiText, festival) = getTodayTithiAndFestival(context)
        views.setTextViewText(R.id.widget_month_tithi, tithiText)
        
        // 2. વાર
        val todayDay = getGujaratiDay()
        views.setTextViewText(R.id.widget_day, todayDay)
        
        // 3. રાશિ
        val rashi = getTodayRashi()
        views.setTextViewText(R.id.widget_rashi, "⭐ $rashi")
        
        // 4. ચોઘડિયુ
        val choghadiya = calculateChoghadiya()
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
    
    // તિથિ અને તહેવાર CSVમાંથી
    private fun getTodayTithiAndFestival(context: Context): Pair<String, String> {
        try {
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(Calendar.getInstance().time)
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line?.split(",")
                if (parts != null && parts.size > 4) {
                    val date = parts[0].trim()
                    val month = parts[1].trim()
                    val tithi = parts[2].trim()
                    val festival = parts[3].trim()
                    
                    if (date == today) {
                        reader.close()
                        val tithiText = "$month $tithi"
                        return Pair(tithiText, festival)
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair("માગશર વદ-૩", "")
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
    
    // ચોઘડિયુ ગણતરી
    private fun calculateChoghadiya(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        val totalMinutes = hour * 60 + minute
        val periodMinutes = 96
        val periodIndex = (totalMinutes / periodMinutes) % 8
        
        val choghadiyaList = arrayOf(
            "અમૃત", "ચલ", "લાભ", "શુભ", 
            "રોગ", "કાલ", "ઉદ્વેગ", "લાભ"
        )
        
        return choghadiyaList[periodIndex]
    }
}
