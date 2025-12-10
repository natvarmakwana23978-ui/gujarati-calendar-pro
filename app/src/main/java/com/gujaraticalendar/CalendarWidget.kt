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
        
        // 1. વિક્રમ સંવત (હજુ એવું જ)
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
        
        // 4. ચોઘડિયુ (હજુ એવું જ)
        views.setTextViewText(R.id.widget_choghadiya, "લાભ")
        
        appWidgetManager.updateAppWidget(widgetId, views)
    }
    
    // CSV વાંચવાનું નવું ફંક્શન
    private fun getTodayTithiFromCSV(context: Context): String {
        try {
            // CSV ફાઈલ ખોલો
            val inputStream = context.assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            // આજની તારીખ બનાવો (2025/12/10)
            val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(Calendar.getInstance().time)
            
            // દરેક લાઈન વાંચો
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line?.split(",")
                if (parts != null && parts.size > 2) {
                    val date = parts[0].trim()
                    val month = parts[1].trim()
                    val tithi = parts[2].trim()
                    
                    // જો આજની તારીખ મળી
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
        
        // ન મળે તો ડિફૉલ્ટ
        return "માગશર વદ-૩"
    }
}
