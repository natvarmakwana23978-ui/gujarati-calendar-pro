
package com.gujaraticalendar

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class CalendarWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // For each widget that belongs to this provider
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        // Create RemoteViews object for widget layout
        val views = RemoteViews(context.packageName, R.layout.widget_simple1)
        
        // Set text in widget
        views.setTextViewText(R.id.widget_date, "ગુજરાતી કેલેન્ડર")
        views.setTextViewText(R.id.widget_festival, "વિક્રમ સંવત ૨૦૮૨")
        
        // Update the widget
        appWidgetManager.updateAppWidget(widgetId, views)
    }
}
