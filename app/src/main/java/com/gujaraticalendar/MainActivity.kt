package com.gujaraticalendar

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 5 UI એલિમેન્ટ્સ
        val tvSamvat: TextView = findViewById(R.id.tv_samvat)     // વિક્રમ સંવત
        val tvMonthTithi: TextView = findViewById(R.id.tv_month_tithi) // માગશર વદ - આઠમ
        val tvDay: TextView = findViewById(R.id.tv_day)           // શુક્રવાર
        val tvChoghadiya: TextView = findViewById(R.id.tv_choghadiya) // લાભ
        val tvEvent: TextView = findViewById(R.id.tv_event)       // તહેવાર
        
        // CSV લોડર બનાવો
        val csvLoader = CsvLoader(this)
        
        // ડેટા બતાવો
        showPanchangData(csvLoader, tvSamvat, tvMonthTithi, tvDay, tvChoghadiya, tvEvent)
    }
    GujaratiCalendarWidget.refreshWidget(this)
}
    private fun showPanchangData(
        csvLoader: CsvLoader,
        tvSamvat: TextView,
        tvMonthTithi: TextView,
        tvDay: TextView,
        tvChoghadiya: TextView,
        tvEvent: TextView
    ) {
        // 1. વિક્રમ સંવત (સ્થિર)
        tvSamvat.text = "વિક્રમ સંવત - ૨૦૮૨"
        
        // 2. CSVમાંથી તિથિ-મહિનો
        val panchangData = csvLoader.getTodayPanchang()
        
        Log.d("MAIN_ACTIVITY", "CSVમાંથી મળેલો ડેટા: ${if (panchangData != null) "હા" else "ના"}")
        
        if (panchangData != null) {
            // CSV ડેટા મળ્યું
            Log.d("MAIN_ACTIVITY", "તારીખ: ${panchangData.date}")
            Log.d("MAIN_ACTIVITY", "મહિનો: ${panchangData.month}")
            Log.d("MAIN_ACTIVITY", "તિથિ: ${panchangData.tithiName}")
            Log.d("MAIN_ACTIVITY", "વાર: ${panchangData.dayOfWeek}")
            Log.d("MAIN_ACTIVITY", "સૂર્યોદય: ${panchangData.sunrise}")
            Log.d("MAIN_ACTIVITY", "સૂર્યાસ્ત: ${panchangData.sunset}")
            Log.d("MAIN_ACTIVITY", "ઇવેન્ટ: ${panchangData.eventName}")
            
            val monthName = panchangData.month
            val tithiName = panchangData.tithiName
            
            // તિથિ ફોર્મેટ કરો
            val formattedTithi = csvLoader.formatTithi(tithiName)
            
            // મહિનો અને તિથિ એકસાથે
            tvMonthTithi.text = "$monthName $formattedTithi"
            
            // વાર (CSV ડેટામાંથી)
            tvDay.text = panchangData.dayOfWeek
            
            // ચોઘડિયો (કેલ્ક્યુલેટ કરો)
            val choghadiya = csvLoader.calculateChoghadiya(panchangData)
            tvChoghadiya.text = choghadiya
            Log.d("MAIN_ACTIVITY", "ચોઘડિયું: $choghadiya")
            
            // તહેવાર
            val event = if (panchangData.eventName.isNotEmpty()) panchangData.eventName else ""
            tvEvent.text = event
            
        } else {
            // CSV ડેટા ન મળે
            Log.e("MAIN_ACTIVITY", "CSVમાં આજની તારીખ મળી નથી!")
            
            // ડિફૉલ્ટ ડેટા બતાવો
            tvMonthTithi.text = "તિથિ ઉપલબ્ધ નથી"
            
            // આજનો વાર
            val calendar = Calendar.getInstance()
            val dayNames = arrayOf(
                "રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર",
                "ગુરુવાર", "શુક્રવાર", "શનિવાર"
            )
            tvDay.text = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            
            // ડિફૉલ્ટ ચોઘડિયું
            tvChoghadiya.text = "કાળ"
            tvEvent.text = "CSV ડેટા ઉપલબ્ધ નથી"
        }
    }
}
