package com.gujaraticelendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // એકદમ સરળ
        val textView = TextView(this)
        textView.text = "ગુજરાતી કેલેન્ડર એપ"
        textView.textSize = 24f
        setContentView(textView)
    }
}
