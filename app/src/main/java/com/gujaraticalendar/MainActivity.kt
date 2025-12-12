package com.yourpackage.gujaraticalendar // તમારું પેકેજ

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        Log.d("APP", "ગુજરાતી પંચાંગ એપ શરૂ")
        
        // CSV લોડર બનાવો
        val csvLoader = CsvLoader(this)
        
        // CSV ટેસ્ટ કરો
        testCsvLoader(csvLoader)
    }
    
    private fun testCsvLoader(csvLoader: CsvLoader) {
        Log.d("CSV_TEST", "=== CSV ફાઈલ ચકાસણી ===")
        
        try {
            // ટેસ્ટ 1: ચોક્કસ તારીખ
            val testDate = "2025/10/22"
            val data = csvLoader.getPanchangForDate(testDate)
            
            if (data != null) {
                Log.d("CSV_TEST", "✅ ટેસ્ટ 1 સફળ")
                Log.d("CSV_TEST", "   તારીખ: ${data.date}")
                Log.d("CSV_TEST", "   તિથિ: ${data.tithiName}")
                Log.d("CSV_TEST", "   મહિનો: ${data.month}")
                Log.d("CSV_TEST", "   તહેવાર: ${data.eventName}")
                Log.d("CSV_TEST", "   સૂર્યોદય: ${data.sunrise}")
            } else {
                Log.e("CSV_TEST", "❌ ટેસ્ટ 1 નિષ્ફળ: $testDate")
            }
            
            // ટેસ્ટ 2: કુલ ડેટા
            val allData = csvLoader.loadPanchangData()
            Log.d("CSV_TEST", "📊 કુલ એન્ટ્રીઓ: ${allData.size}")
            
            // પહેલી 2 એન્ટ્રીઓ બતાવો
            var count = 0
            for ((date, item) in allData) {
                if (count < 2) {
                    Log.d("CSV_TEST", "  ${count+1}. $date → ${item.tithiName}")
                    count++
                } else {
                    break
                }
            }
            
        } catch (e: Exception) {
            Log.e("CSV_TEST", "💥 ભૂલ: ${e.message}")
        }
        
        Log.d("CSV_TEST", "=== ચકાસણી પૂર્ણ ===")
    }
}
