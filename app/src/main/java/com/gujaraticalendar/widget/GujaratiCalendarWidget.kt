package com.gujaraticalendar.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.gujaraticalendar.R
import java.text.SimpleDateFormat
import java.util.*

class GujaratiCalendarWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // બધા વિજેટ્સને અપડેટ કરો
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // વિજેટનું layout લોડ કરો
        val views = RemoteViews(context.packageName, R.layout.widget_simple1)

        // આજની તારીખ મેળવો
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date())
        
        // CSV માંથી ડેટા મેળવો (સરળ માટે hardcoded)
        val calendarData = getCalendarData(today)

        // વિજેટમાં ડેટા સેટ કરો
        views.setTextViewText(R.id.txt_vikram_samvat, "વિક્રમ સંવત ૨૦૮૨")
        views.setTextViewText(R.id.txt_gujarati_month, calendarData.gujaratiMonth)
        views.setTextViewText(R.id.txt_tithi, calendarData.tithi)
        views.setTextViewText(R.id.txt_day, getDayOfWeek())
        views.setTextViewText(R.id.txt_paksha, getPaksha(calendarData.tithi))
        views.setTextViewText(R.id.txt_choghadiya, getChoghadiya())
        
        if (calendarData.festival.isNotEmpty()) {
            views.setTextViewText(R.id.txt_festival, calendarData.festival)
        } else {
            views.setTextViewText(R.id.txt_festival, "")
        }

        // વિજેટ અપડેટ કરો
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // CSV ડેટા (આગળે ફાઈલમાંથી વાંચીશું)
    private fun getCalendarData(date: String): CalendarData {
        // Hardcoded ડેટા (આગળે CSV પાર્સ કરીશું)
        return when (date) {
            "2025/10/22" -> CalendarData("કારતક", "સુદ-૧", "બેસતુ વર્ષ")
            "2025/10/23" -> CalendarData("કારતક", "સુદ-ર", "")
            "2025/10/24" -> CalendarData("કારતક", "સુદ-૩", "")
            "2025/10/25" -> CalendarData("કારતક", "સુદ-૪", "")
            "2025/10/26" -> CalendarData("કારતક", "સુદ-પ", "")
            else -> CalendarData("કારતક", "સુદ-૧", "")
        }
    }

    private fun getDayOfWeek(): String {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.SUNDAY -> "રવિવાર"
            Calendar.MONDAY -> "સોમવાર"
            Calendar.TUESDAY -> "મંગળવાર"
            Calendar.WEDNESDAY -> "બુધવાર"
            Calendar.THURSDAY -> "ગુરુવાર"
            Calendar.FRIDAY -> "શુક્રવાર"
            Calendar.SATURDAY -> "શનિવાર"
            else -> "રવિવાર"
        }
    }

    private fun getPaksha(tithi: String): String {
        return if (tithi.startsWith("સુદ")) "સુદ પક્ષ" else "વદ પક્ષ"
    }

    private fun getChoghadiya(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 6..8 -> "લાભ ચોઘડીયા"
            hour in 9..11 -> "શુભ ચોઘડીયા"
            hour in 12..14 -> "અમૃત ચોઘડીયા"
            hour in 15..17 -> "ચલ ચોઘડીયા"
            else -> "અમૃત ચોઘડીયા"
        }
    }

    data class CalendarData(
        val gujaratiMonth: String,
        val tithi: String,
        val festival: String
    )
}
