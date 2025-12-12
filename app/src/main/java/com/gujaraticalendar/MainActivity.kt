package com.gujaraticalendar

// ==== ADD THESE IMPORTS ====
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileNotFoundException
// ===========================
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    private fun getFirstAvailableDate(): String {
    // આ function તમને જોઈતું હોય તે date return કરે
    return "2024-12-01" // તમારું date અહીં મૂકો
}
    // === CSV ડિબગ ===
    Log.d("CSV_DEBUG", "=== CSV ડિબગ શરૂ ===")
    
    // 1. Assets લિસ્ટિંગ
    try {
        val assetFiles = assets.list("")
        Log.d("CSV_DEBUG", "📁 Assets ફાઈલ્સ: ${assetFiles?.joinToString()}")
    } catch (e: Exception) {
        Log.e("CSV_DEBUG", "❌ Assets લિસ્ટિંગ ભૂલ: ${e.message}")
    }
    
    // 2. CSV ફાઈલ વાંચવાનો પ્રયાસ
    try {
        val inputStream = assets.open("calendar_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        
        // હેડર
        val header = reader.readLine()
        Log.d("CSV_DEBUG", "📋 CSV હેડર: $header")
        
        // પહેલી 3 લાઇન
        for (i in 1..3) {
            val line = reader.readLine()
            if (line != null) {
                Log.d("CSV_DEBUG", "📝 લાઇન $i: $line")
            }
        }
        
        reader.close()
        Log.d("CSV_DEBUG", "✅ CSV વાંચવામાં સફળ")
        
    } catch (e: FileNotFoundException) {
        Log.e("CSV_DEBUG", "❌ CSV ફાઈલ ન મળી: calendar_data.csv")
        Log.e("CSV_DEBUG", "🔍 Assets path: ${assets.list("")?.joinToString()}")
    } catch (e: Exception) {
        Log.e("CSV_DEBUG", "❌ CSV વાંચવામાં ભૂલ: ${e.message}")
    }
    
    Log.d("CSV_DEBUG", "=== CSV ડિબગ પૂર્ણ ===")
    
    // ... તમારો બાકીનો કોડ
    val csvLoader = CsvLoader(this)
    val btnShowTithi: Button = findViewById(R.id.btn_show_tithi)
    // ... બાકી
}
        val csvLoader = CsvLoader(this)
        
        // UI એલિમેન્ટ્સ શોધો
        val tvTithi: TextView = findViewById(R.id.tv_tithi)
        val tvRashi: TextView = findViewById(R.id.tv_rashi)
        val tvMonth: TextView = findViewById(R.id.tv_month)
        val tvSunrise: TextView = findViewById(R.id.tv_sunrise)
        val tvEvent: TextView = findViewById(R.id.tv_event)
        val tvStatus: TextView = findViewById(R.id.tv_status)
        val btnShowTithi: Button = findViewById(R.id.btn_show_tithi)
        
        // બટન પર ક્લિક થાય ત્યારે CSV ડેટા બતાવો
        btnShowTithi.setOnClickListener {
            showCsvData(csvLoader, tvTithi, tvRashi, tvMonth, tvSunrise, tvEvent, tvStatus)
        }
        
        // એપ શરૂ થાય ત્યારે પણ CSV ડેટા બતાવો
        showCsvData(csvLoader, tvTithi, tvRashi, tvMonth, tvSunrise, tvEvent, tvStatus)
    }
    
    private fun showCsvData(
        csvLoader: CsvLoader,
        tvTithi: TextView,
        tvRashi: TextView,
        tvMonth: TextView,
        tvSunrise: TextView,
        tvEvent: TextView,
        tvStatus: TextView
    ) {
        // CSVમાંથી ડેટા લાવો (આજની તારીખ અથવા પહેલી ઉપલબ્ધ)
        val panchangData = csvLoader.getTodayPanchang() ?: csvLoader.getFirstAvailableDate()
        
        if (panchangData != null) {
            // CSV ડેટા UI માં બતાવો
            tvTithi.text = "🌙 તિથિ: ${panchangData.tithiName}"
            tvMonth.text = "🗓️ મહિનો: ${panchangData.month}"
            tvSunrise.text = "☀️ સૂર્યોદય: ${panchangData.sunrise.substring(0, 5)}"
            
            // તહેવાર (જો હોય)
            if (panchangData.eventName.isNotBlank()) {
                tvEvent.text = "🎉 ${panchangData.eventName}"
                tvEvent.visibility = View.VISIBLE
            } else {
                tvEvent.visibility = View.GONE
            }
            
            // સ્ટેટસ અને રાશિ
            tvStatus.text = "✅ CSV ડેટા લોડ થયો: ${panchangData.date}"
            tvRashi.text = "✨ રાશિ: મેષ" // હજુ CSVમાં નથી
            
            // સફળતાનો મેસેજ
            Toast.makeText(this, "તિથિ: ${panchangData.tithiName}", Toast.LENGTH_SHORT).show()
            
        } else {
            // CSV ડેટા ન મળે તો
            tvTithi.text = "🌙 તિથિ: પ્રતિપ્રદા"
            tvRashi.text = "✨ રાશિ: મેષ"
            tvMonth.text = "🗓️ મહિનો: ચૈત્ર"
            tvSunrise.text = "☀️ સૂર્યોદય: 06:00"
            tvStatus.text = "⚠️ CSV ડેટા મળ્યો નથી"
            tvEvent.visibility = View.GONE
            
            Toast.makeText(this, "CSV ડેટા મળ્યો નથી", Toast.LENGTH_SHORT).show()
        }
    }
}
