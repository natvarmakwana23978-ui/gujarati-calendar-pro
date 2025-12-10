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
        
        // 📌 1. વિક્રમ સંવત (હાર્ડકોડ હવે લાંબા સમય માટે)
        views.setTextViewText(R.id.widget_vikram_samvat, "વિક્રમ સંવત ૨૦૮૨")
        
        // 📌 2. માસ-તિથિ (અહીં હાર્ડકોડ છે, પછી લોજિક ઉમેરશો)
        views.setTextViewText(R.id.widget_month_tithi, "માગશર વદ-૩")
        
        // 📌 3. વાર (આજનો વાર ગણતરી)
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val gujaratiDays = arrayOf("રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર", "ગુરુવાર", "શુક્રવાર", "શનિવાર")
        val todayDay = gujaratiDays[dayOfWeek - 1]
        views.setTextViewText(R.id.widget_day, todayDay)
        
        // 📌 4. ચોઘડિયુ (અહીં હાર્ડકોડ છે, પછી લોજિક ઉમેરશો)
        views.setTextViewText(R.id.widget_choghadiya, "ચોઘડિયુ: લાભ")
        
        appWidgetManager.updateAppWidget(widgetId, views)
    }
}
