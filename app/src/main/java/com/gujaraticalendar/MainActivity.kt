package com.gujaraticalendar

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var dateTextView: TextView
    private lateinit var addWidgetButton: Button
    private lateinit var birthdayButton: Button
    private lateinit var festivalButton: Button
    private lateinit var festivalListTextView: TextView
    
    // CSV કોલમની સંખ્યા
    companion object {
        const val COL_DATE = 0
        const val COL_MONTH = 1
        const val COL_TITHI = 2
        const val COL_FESTIVAL = 3
        const val COL_TYPE = 4
        const val COL_SUNRISE = 5
        const val COL_SUNSET = 6
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        Log.d("CALENDAR_APP", "એપ શરૂ થઈ")
        
        // Initialize views
        dateTextView = findViewById(R.id.date_text_view)
        addWidgetButton = findViewById(R.id.add_widget_button)
        birthdayButton = findViewById(R.id.birthday_button)
        festivalButton = findViewById(R.id.festival_button)
        festivalListTextView = findViewById(R.id.festillerym)
        
        // Set today's date and find today's events
        setTodaysDateAndEvents()
        
        // Set up button click listeners
        setupButtonListeners()
        
        Toast.makeText(this, "ગુજરાતી કેલેન્ડર એપ શરૂ થઈ!", Toast.LENGTH_LONG).show()
    }
    
    private fun setTodaysDateAndEvents() {
        try {
            // આજની તારીખ (YYYY/MM/DD ફોર્મેટમાં)
            val todayFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val todayStr = todayFormat.format(Date())
            
            // ડિસ્પ્લે માટે સુંદર તારીખ
            val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale("gu"))
            val displayDate = displayFormat.format(Date())
            
            // CSV ડેટા વાંચો
            val csvData = readCSVFromAssets()
            var todayEventFound = false
            
            // આજની તારીખનો ડેટા શોધો
            for (record in csvData) {
                if (record.size > COL_DATE && record[COL_DATE] == todayStr) {
                    // આજનો ડેટા મળ્યો
                    val gujaratiMonth = record.getOrElse(COL_MONTH) { "" }
                    val tithi = record.getOrElse(COL_TITHI) { "" }
                    val festival = record.getOrElse(COL_FESTIVAL) { "" }
                    val festivalType = record.getOrElse(COL_TYPE) { "" }
                    
                    val displayText = StringBuilder()
                    displayText.append("ગુજરાતી કેલેન્ડર\n")
                    displayText.append("વિક્રમ સંવત ૨૦૮૨\n\n")
                    displayText.append("આજની તારીખ: $displayDate\n")
                    displayText.append("મહિનો: $gujaratiMonth\n")
                    displayText.append("તિથિ: $tithi\n")
                    
                    if (festival.isNotEmpty()) {
                        displayText.append("\n✨ $festival")
                        if (festivalType.isNotEmpty()) {
                            displayText.append(" ($festivalType)")
                        }
                        todayEventFound = true
                    } else {
                        displayText.append("\n📅 આજે કોઈ ખાસ તહેવાર નથી")
                    }
                    
                    dateTextView.text = displayText.toString()
                    break
                }
            }
            
            if (!todayEventFound) {
                dateTextView.text = "ગુજરાતી કેલેન્ડર\nવિક્રમ સંવત ૨૦૮૨\n\nઆજની તારીખ: $displayDate\n\nડેટા લોડ કરી રહ્યા છીએ..."
            }
            
            // આગામી 3 તહેવારો બતાવો
            showUpcomingFestivals(csvData, todayStr)
            
        } catch (e: Exception) {
            dateTextView.text = "ગુજરાતી કેલેન્ડર\n\nતારીખ મેળવવામાં એરર"
            Log.e("CALENDAR_APP", "એરર: ${e.message}")
            festivalListTextView.text = "ડેટા લોડ કરવામાં સમસ્યા આવી"
        }
    }
    
    private fun readCSVFromAssets(): List<List<String>> {
        val data = mutableListOf<List<String>>()
        try {
            Log.d("CALENDAR_APP", "CSV વાંચવાનું શરૂ કર્યું")
            
            val inputStream = assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            
            // પ્રથમ લાઈન (header) છોડો
            reader.readLine()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    // Comma-separated values પાર્સ કરો
                    val parts = it.split(",").map { part -> part.trim() }
                    if (parts.isNotEmpty() && parts[0].isNotEmpty()) {
                        data.add(parts)
                    }
                }
            }
            
            Log.d("CALENDAR_APP", "${data.size} રેકોર્ડ વાંચ્યા")
            
        } catch (e: Exception) {
            Log.e("CALENDAR_APP", "CSV વાંચવામાં એરર: ${e.message}")
        }
        return data
    }
    
    private fun showUpcomingFestivals(csvData: List<List<String>>, todayStr: String) {
        try {
            val upcomingFestivals = mutableListOf<String>()
            var todayFound = false
            
            for (record in csvData) {
                if (record.size > COL_DATE) {
                    val date = record[COL_DATE]
                    val festival = record.getOrElse(COL_FESTIVAL) { "" }
                    
                    if (date == todayStr) {
                        todayFound = true
                        continue
                    }
                    
                    // આજથી પછીના તહેવારો
                    if (todayFound && festival.isNotEmpty()) {
                        // તારીખ ફોર્મેટ બદલો (2025/10/22 → 22-10-2025)
                        val dateParts = date.split("/")
                        if (dateParts.size == 3) {
                            val formattedDate = "${dateParts[2]}-${dateParts[1]}-${dateParts[0]}"
                            upcomingFestivals.add("$formattedDate: $festival")
                        } else {
                            upcomingFestivals.add("$date: $festival")
                        }
                        
                        if (upcomingFestivals.size >= 5) {
                            break
                        }
                    }
                }
            }
            
            if (upcomingFestivals.isNotEmpty()) {
                festivalListTextView.text = "આગામી તહેવારો:\n\n" + 
                    upcomingFestivals.joinToString("\n")
            } else {
                festivalListTextView.text = "આગામી તહેવારો:\n\nગુજરાતી નવું વર્ષ\nઉગાડી\nરામ નવમી\nમહાવીર જયંતી\nએકમ"
            }
            
        } catch (e: Exception) {
            festivalListTextView.text = "આગામી તહેવારો:\n\nડેટા લોડ કરવામાં સમસ્યા"
            Log.e("CALENDAR_APP", "ઉપકમિંગ ફેસ્ટિવલ એરર: ${e.message}")
        }
    }
    
    private fun setupButtonListeners() {
        Log.d("CALENDAR_APP", "બટન લિસ્નર સેટ કરી રહ્યા છીએ")
        
        addWidgetButton.setOnClickListener {
            Log.d("CALENDAR_APP", "વિજેટ બટન ક્લિક")
            Toast.makeText(this, "વિજેટ ફીચર આવનાર છે...", Toast.LENGTH_LONG).show()
            festivalListTextView.text = "વિજેટ ફીચર:\n\nઆ એપનો વિજેટ ઝડપથી ઉપલબ્ધ થશે!"
        }
        
        birthdayButton.setOnClickListener {
            Log.d("CALENDAR_APP", "જન્મદિવસ બટન ક્લિક")
            Toast.makeText(this, "જન્મદિવસ ડેટા લોડ કરી રહ્યા છીએ...", Toast.LENGTH_LONG).show()
            
            try {
                val csvData = readCSVFromAssets()
                val birthdays = mutableListOf<String>()
                
                for (record in csvData) {
                    if (record.size > COL_FESTIVAL) {
                        val festival = record[COL_FESTIVAL]
                        val date = record.getOrElse(COL_DATE) { "" }
                        
                        // જન્મદિવસ શબ્દો શોધો
                        if (festival.contains("જયંતિ") || 
                            festival.contains("જયંતી") || 
                            festival.contains("જન્મદિવસ")) {
                            
                            val dateParts = date.split("/")
                            if (dateParts.size == 3) {
                                val formattedDate = "${dateParts[2]}-${dateParts[1]}"
                                birthdays.add("$formattedDate: $festival")
                            }
                            
                            if (birthdays.size >= 5) break
                        }
                    }
                }
                
                if (birthdays.isNotEmpty()) {
                    festivalListTextView.text = "જન્મદિવસ / જયંતિ:\n\n" + 
                        birthdays.joinToString("\n")
                } else {
                    festivalListTextView.text = "જન્મદિવસ:\n\nગાંધી જયંતી (૨-૧૦)\nસરદાર જયંતી (૩૧-૧૦)\nભગતસિંહ જયંતી (૨૮-૯)"
                }
                
            } catch (e: Exception) {
                festivalListTextView.text = "જન્મદિવસ ડેટા લોડ કરી શકાયો નહીં"
            }
        }
        
        festivalButton.setOnClickListener {
            Log.d("CALENDAR_APP", "તહેવાર બટન ક્લિક")
            Toast.makeText(this, "તમામ તહેવારો લોડ કરી રહ્યા છીએ...", Toast.LENGTH_LONG).show()
            
            try {
                val csvData = readCSVFromAssets()
                val allFestivals = mutableListOf<String>()
                
                for (record in csvData) {
                    if (record.size > COL_FESTIVAL) {
                        val festival = record[COL_FESTIVAL]
                        val date = record.getOrElse(COL_DATE) { "" }
                        val type = record.getOrElse(COL_TYPE) { "" }
                        
                        if (festival.isNotEmpty() && type == "તહેવાર") {
                            val dateParts = date.split("/")
                            if (dateParts.size == 3) {
                                val formattedDate = "${dateParts[2]}-${dateParts[1]}"
                                allFestivals.add("$formattedDate: $festival")
                            }
                            
                            if (allFestivals.size >= 10) break
                        }
                    }
                }
                
                if (allFestivals.isNotEmpty()) {
                    festivalListTextView.text = "મુખ્ય તહેવારો:\n\n" + 
                        allFestivals.joinToString("\n")
                } else {
                    festivalListTextView.text = "તહેવારો:\n\nદિવાળી\nધનતેરસ\nકાળી ચૌદશ\nનરક ચતુર્દશી\nછોટી દિવાળી"
                }
                
            } catch (e: Exception) {
                festivalListTextView.text = "તહેવાર ડેટા લોડ કરી શકાયો નહીં"
            }
        }
    }
}
