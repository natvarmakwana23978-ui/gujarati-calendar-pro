package com.gujaraticalendar

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class GujaratiCalendarWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        
        // મુખ્ય એપ ખોલવા માટે ક્લિક ઇવેન્ટ
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        
        try {
            // CSV લોડર બનાવો
            val csvLoader = CsvLoader(context)
            
            // 1. વિક્રમ સંવત
            views.setTextViewText(R.id.widget_samvat, "વિક્રમ સંવત - ૨૦૮૨")
            
            // 2. CSVમાંથી પંચાંગ ડેટા
            val panchangData = csvLoader.getTodayPanchang()
            
            if (panchangData != null) {
                // મહિનો અને તિથિ
                val monthName = panchangData.month
                val tithiName = panchangData.tithiName
                val formattedTithi = csvLoader.formatTithi(tithiName)
                views.setTextViewText(R.id.widget_month_tithi, "$monthName $formattedTithi")
                
                // વાર
                views.setTextViewText(R.id.widget_day, panchangData.dayOfWeek)
                
                // ચોઘડિયું
                val choghadiya = csvLoader.calculateChoghadiya(panchangData)
                views.setTextViewText(R.id.widget_choghadiya, choghadiya)
                
                // તહેવાર
                val event = if (panchangData.eventName.isNotEmpty()) {
                    panchangData.eventName
                } else {
                    ""
                }
                views.setTextViewText(R.id.widget_event, event)
                
            } else {
                // CSV ડેટા ન મળે તો
                views.setTextViewText(R.id.widget_month_tithi, "માગશર વદ - આઠમ")
                
                // આજનો વાર
                val calendar = Calendar.getInstance()
                val dayNames = arrayOf(
                    "રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર",
                    "ગુરુવાર", "શુક્રવાર", "શનિવાર"
                )
                views.setTextViewText(R.id.widget_day, dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1])
                
                // ડિફૉલ્ટ ચોઘડિયું
                views.setTextViewText(R.id.widget_choghadiya, "લાભ")
                views.setTextViewText(R.id.widget_event, "")
            }
            
        } catch (e: Exception) {
            // ભૂલ થાય તો ડિફૉલ્ટ ડેટા
            views.setTextViewText(R.id.widget_month_tithi, "માગશર વદ - આઠમ")
            views.setTextViewText(R.id.widget_day, "શુક્રવાર")
            views.setTextViewText(R.id.widget_choghadiya, "લાભ")
        }
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context) {
        // પહેલી વાર વિજેટ ઍડ થાય ત્યારે
        updateAllWidgets(context)
    }

    override fun onDisabled(context: Context) {
        // છેલ્લું વિજેટ રીમૂવ થાય ત્યારે
    }

    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, GujaratiCalendarWidget::class.java)
        )
        
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun refreshWidget(context: Context) {
            val intent = Intent(context, GujaratiCalendarWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, GujaratiCalendarWidget::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            context.sendBroadcast(intent)
        }
    }
}
