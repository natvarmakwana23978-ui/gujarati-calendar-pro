package com.gujaraticalendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // UI એલિમેન્ટ્સ
        val tvSamvat: TextView = findViewById(R.id.tv_samvat)
        val tvMonthTithi: TextView = findViewById(R.id.tv_month_tithi)
        val tvDay: TextView = findViewById(R.id.tv_day)
        val tvChoghadiya: TextView = findViewById(R.id.tv_choghadiya)
        val tvEvent: TextView = findViewById(R.id.tv_event)
        
        // CSV લોડર
        val csvLoader = CsvLoader(this)
        
        // ડેટા સેટ કરો
        tvSamvat.text = csvLoader.getVikramSamvat()
        tvMonthTithi.text = "માગશર વદ - આઠમ"
        tvDay.text = "શુક્રવાર"
        tvChoghadiya.text = "લાભ"
        tvEvent.text = ""
    }
}
