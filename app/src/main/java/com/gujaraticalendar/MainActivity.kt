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
        
        // Set today's date
        setTodaysDate()
        
        // Read CSV data
        readAndDisplayCSV()
        
        // Set up button click listeners
        setupButtonListeners()
        
        Toast.makeText(this, "ગુજરાતી કેલેન્ડર એપ શરૂ થઈ!", Toast.LENGTH_LONG).show()
    }
    
    private fun setTodaysDate() {
        try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("gu"))
            val today = dateFormat.format(Date())
            dateTextView.text = "ગુજરાતી કેલેન્ડર\nવિક્રમ સંવત ૨૦૮૨\n\nઆજની તારીખ: $today"
        } catch (e: Exception) {
            dateTextView.text = "ગુજરાતી કેલેન્ડર\nતારીખ મેળવવામાં એરર"
            Log.e("CALENDAR_APP", "તારીખ એરર: ${e.message}")
        }
    }
    
    private fun readAndDisplayCSV() {
        try {
            Log.d("CALENDAR_APP", "CSV વાંચવાનું શરૂ કર્યું")
            
            // Read CSV file
            val inputStream = assets.open("calendar_data.csv")
            val reader = inputStream.bufferedReader()
            val lines = reader.readLines()
            
            if (lines.size > 1) {
                val events = mutableListOf<String>()
                
                for (i in 1 until minOf(6, lines.size)) {
                    val line = lines[i]
                    val parts = line.split(",")
                    if (parts.size >= 4) {
                        val date = parts[0]
                        val festival = parts[3]
                        
                        if (festival.isNotBlank()) {
                            val dateParts = date.split("/")
                            if (dateParts.size == 3) {
                                val formattedDate = "${dateParts[2]}-${dateParts[1]}"
                                events.add("$formattedDate: $festival")
                            }
                        }
                    }
                }
                
                if (events.isNotEmpty()) {
                    val displayText = events.joinToString("\n")
                    festivalListTextView.text = "આગામી તહેવારો:\n$displayText"
                } else {
                    showSampleEvents()
                }
            } else {
                showSampleEvents()
            }
            
        } catch (e: Exception) {
            Log.e("CALENDAR_APP", "CSV એરર: ${e.message}")
            showSampleEvents()
        }
    }
    
    private fun showSampleEvents() {
        val sampleEvents = listOf(
            "૨૨-૧૦: શરદ પૂર્ણિમા",
            "૨૩-૧૦: વાલ્મીકિ જયંતિ",
            "૨૪-૧૦: ધનતેરસ",
            "૨૫-૧૦: દિવાળી",
            "૨૬-૧૦: ગોવર્ધન પૂજા"
        )
        
        festivalListTextView.text = "આગામી તહેવારો:\n" + sampleEvents.joinToString("\n")
    }
    
    private fun setupButtonListeners() {
        Log.d("CALENDAR_APP", "બટન લિસ્નર સેટ કરી રહ્યા છીએ")
        
        addWidgetButton.setOnClickListener {
            Log.d("CALENDAR_APP", "વિજેટ બટન ક્લિક")
            
            val message = "વિજેટ ઉમેરવાની રીત:\n" +
                         "1. હોમસ્ક્રીન પર લાંબો દબાવો\n" +
                         "2. 'Widgets' પસંદ કરો\n" +
                         "3. 'Gujarati Calendar' શોધો\n" +
                         "4. વિજેટને હોમસ્ક્રીન પર ખેંચો"
        
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            
            festivalListTextView.text = "વિજેટ ઉમેરવાની સૂચના:\n\n" +
                                      "1. હોમસ્ક્રીન પર લાંબો દબાવો\n" +
                                      "2. 'Widgets' પસંદ કરો\n" +
                                      "3. 'Gujarati Calendar' શોધો\n" +
                                      "4. વિજેટને હોમસ્ક્રીન પર ખેંચો\n\n" +
                                      "✅ વિજેટ તૈયાર છે!"
        }
        
        birthdayButton.setOnClickListener {
            Log.d("CALENDAR_APP", "જન્મદિવસ બટન ક્લિક")
            Toast.makeText(this, "જન્મદિવસ ડેટા...", Toast.LENGTH_LONG).show()
            
            festivalListTextView.text = "જન્મદિવસ / જયંતિ:\n\n" +
                                      "૦૨-૧૦: ગાંધી જયંતી\n" +
                                      "૩૧-૧૦: સરદાર જયંતી\n" +
                                      "૨૮-૦૯: ભગતસિંહ જયંતી\n" +
                                      "૧૪-૧૧: બાળ દિવસ\n" +
                                      "૨૫-૧૨: આટલ બિહારી વાજપેયી જયંતી"
        }
        
        festivalButton.setOnClickListener {
            Log.d("CALENDAR_APP", "તહેવાર બટન ક્લિક")
            Toast.makeText(this, "તહેવાર ડેટા...", Toast.LENGTH_LONG).show()
            
            festivalListTextView.text = "મુખ્ય તહેવારો:\n\n" +
                                      "૨૪-૧૦: ધનતેરસ\n" +
                                      "૨૫-૧૦: કાળી ચૌદશ\n" +
                                      "૨૬-૧૦: દિવાળી\n" +
                                      "૨૭-૧૦: ગોવર્ધન પૂજા\n" +
                                      "૨૮-૧૦: ભાઈદૂજ\n" +
                                      "૧૨-૧૧: ગુરુ નાનક જયંતી\n" +
                                      "૧૫-૧૧: છઠ પૂજા\n" +
                                      "૨૫-૧૨: ક્રિસમસ"
        }
    }
}
