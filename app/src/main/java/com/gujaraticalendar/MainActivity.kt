package com.gujaraticalendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ✅ એકદમ સરળ - CSV વગર
        val textView = TextView(this)
        textView.text = """
            સ્માર્ટ ગુજરાતી ઘર
            
            આજની તિથિ: પ્રતિપદા
            આજની રાશિ: મેષ
            
            CSV ડેટા તૈયાર થાય ત્યાં સુધી
            હાર્ડકોડેડ ડેટા
        """.trimIndent()
        
        textView.textSize = 20f
        textView.setPadding(50, 50, 50, 50)
        setContentView(textView)
    }
}
