package com.gujaraticalendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // ફક્ત 3 UI એલિમેન્ટ્સની જરૂર છે
        val tvTithi: TextView = findViewById(R.id.tv_tithi)
        val tvMonth: TextView = findViewById(R.id.tv_month)
        val tvEvent: TextView = findViewById(R.id.tv_event)
        
        // CSV લોડર બનાવો
        val csvLoader = CsvLoader(this)
        
        // એપ શરૂ થાય ત્યારે જ CSV ડેટા બતાવો (કોઈ બટન નહીં)
        showSimpleData(csvLoader, tvTithi, tvMonth, tvEvent)
    }
    
    private fun showSimpleData(
        csvLoader: CsvLoader,
        tvTithi: TextView,
        tvMonth: TextView,
        tvEvent: TextView
    ) {
        // CSVમાંથી ડેટા લાવો
        val panchangData = csvLoader.getTodayPanchang() ?: csvLoader.getFirstAvailableDate()
        
        if (panchangData != null) {
            // ફક્ત તિથિ અને મહિનો બતાવો (લેબલ વગર)
            tvTithi.text = panchangData.tithiName  // ઉદા: "વદ-૮"
            tvMonth.text = panchangData.month      // ઉદા: "માગશર"
            
            // તહેવાર (જો હોય)
            if (panchangData.eventName.isNotBlank()) {
                tvEvent.text = panchangData.eventName
                tvEvent.visibility = View.VISIBLE
            } else {
                tvEvent.visibility = View.GONE
            }
        } else {
            // CSV ડેટા ન મળે તો ડિફૉલ્ટ
            tvTithi.text = "પ્રતિપ્રદા"
            tvMonth.text = "ચૈત્ર"
            tvEvent.visibility = View.GONE
        }
    }
}
