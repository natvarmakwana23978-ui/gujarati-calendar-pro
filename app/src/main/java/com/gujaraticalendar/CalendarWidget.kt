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
        
        // 1. વિક્રમ સંવત
        views.setTextViewText(R.id.widget_vikram_samvat, "વિક્રમ સંવત ૨૦૮૨")
        
        // 2. CSVમાંથી આજની તિથિ
        val todayTithi = getTodayTithiFromCSV(context)
        views.setTextViewText(R.id.widget_month_tithi, todayTithi)
        
        // 3. આજનો વાર
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val gujaratiDays = arrayOf("રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર", 
                                   "ગુરુવાર", "શુક્રવાર", "શનિવાર")
        val todayDay = gujaratiDays[dayOfWeek - 1]
        views.setTextViewText(R.id.widget_day, todayDay)
        
        // 4. ચોઘડિયુ (નવી ગણતરી)
        val choghadiya = calculateChoghadiya()
        views.setTextViewText(R.id.widget_choghadiya, choghadiya)
        
        appWidgetManager.updateAppWidget(widgetId, views)
    }
    
    // CSV વાંચવાનું ફંક્શન (પહેલાં જેવું જ)
    private fun getTodayTithiFromCSV(context: Context): String {
        try {
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(Calendar.getInstance().time)
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line?.split(",")
                if (parts != null && parts.size > 2) {
                    val date = parts[0].trim()
                    val month = parts[1].trim()
                    val tithi = parts[2].trim()
                    
                    if (date == today) {
                        reader.close()
                        return "$month $tithi"
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            // કોઈ એરર નથી
        }
        return "માગશર વદ-૩"
    }
    
    // નવું ફંક્શન: ચોઘડિયુ ગણતરી
    private fun calculateChoghadiya(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        // કુલ મિનિટ (દિવસની 0:00 થી)
        val totalMinutes = hour * 60 + minute
        
        // ચોઘડિયુ સમય પીરિયડ (1 ચોઘડિયુ = 96 મિનિટ ≈ 1.6 કલાક)
        val periodMinutes = 96
        val periodsInDay = 8  // 8 ચોઘડિયુ દરરોજ
        
        // કયું પીરિયડ ચાલે છે
        val periodIndex = (totalMinutes / periodMinutes) % periodsInDay
        
        // ચોઘડિયુની યાદી (અમૃત, ચલ, લાભ, શુભ, રોગ, કાલ, ઉદ્વેગ, લાભ)
        val choghadiyaList = arrayOf("અમૃત", "ચલ", "લાભ", "શુભ", "રોગ", "કાલ", "ઉદ્વેગ", "લાભ")
        
        return choghadiyaList[periodIndex]
    }
}
