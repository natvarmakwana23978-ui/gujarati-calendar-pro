package com.gujaraticelendar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // R નો ઉપયોગ ન કરતો સરળ version
        val textView = TextView(this)
        textView.text = "ગુજરાતી કેલેન્ડર એપ"
        setContentView(textView)
    }
}
