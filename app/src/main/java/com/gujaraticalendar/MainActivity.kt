package com.gujaraticelendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ✅ સરળ અને safe version
        try {
            // એકદમ સરળ UI
            val textView = android.widget.TextView(this)
            textView.text = "ગુજરાતી કેલેન્ડર\nએપ્લિકેશન"
            textView.textSize = 20f
            textView.gravity = android.view.Gravity.CENTER
            setContentView(textView)
            
        } catch (e: Exception) {
            // Error handle
            e.printStackTrace()
        }
    }
}
