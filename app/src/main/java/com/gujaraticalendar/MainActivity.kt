package com.gujaraticalendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
    
    private fun showPanchangData(
        csvLoader: CsvLoader,
        tvSamvat: TextView,
        tvMonthTithi: TextView,
        tvDay: TextView,
        tvChoghadiya: TextView,
        tvEvent: TextView
    ) {
        // 1. વિક્રમ સંવત (સ્થિર)
        tvSamvat.text = "વિક્રમ સંવત - ૨૦૮૧"
        
        // 2. CSVમાંથી તિથિ-મહિનો
        val panchangData = csvLoader.getTodayPanchang() ?: csvLoader.getFirstAvailableDate()
        
        val monthName = panchangData?.month ?: "માગશર"
        val tithiName = panchangData?.tithiName ?: "વદ-૮"
        
        // "વદ-૮" ને "વદ - આઠમ" બનાવો
        val formattedTithi = formatTithi(tithiName)
        
        // મહિનો અને તિથિ એકસાથે
        tvMonthTithi.text = "$monthName $formattedTithi"
        
        // 3. વાર (હાર્ડકોડ)
        tvDay.text = "શુક્રવાર"
        
        // 4. ચોઘડિયો (હાર્ડકોડ)
        tvChoghadiya.text = "લાભ"
        
        // 5. તહેવાર (જો CSVમાં હોય)
        tvEvent.text = panchangData?.eventName ?: ""
    }
    
    // "વદ-૮" -> "વદ - આઠમ" બનાવવાની ફંક્શન
    private fun formatTithi(tithi: String): String {
        return when (tithi) {
            "વદ-૮" -> "વદ - આઠમ"
            "શુક-૧" -> "શુક - પ્રથમ"
            // અહીં બીજા તિથિ ઉમેરો
            else -> tithi
        }
    }
}
