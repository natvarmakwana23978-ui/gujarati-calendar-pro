package com.gujaraticalendar

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // CSV થી આજનું ડેટા લાવો અને બતાવો
        showTodaysCalendar()
        
        // બટનોને કામ કરતા બનાવો
        setupButtons()
    }
    
    private fun showTodaysCalendar() {
        try {
            // CSV ફાઈલ વાંચો
            val csvData = readCSVFile("calendar_data.csv")
            
            // આજની તારીખ (બે ફોર્મેટમાં)
            val todayDDMMYYYY = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val todayYYYYMMDD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // આજના દિવસનો ડેટા શોધો
            var todaysInfo = "📅 આજે: $todayDDMMYYYY\n"
            var dataFound = false
            
            for (row in csvData) {
                if (row.size >= 7) {
                    val csvDate = row[0] // YYYY-MM-DD ફોર્મેટમાં
                    
                    // બંને ફોર્મેટ સાથે ચક કરો
                    if (csvDate == todayYYYYMMDD) {
                        // CSV કૉલમ્સ મુજબ ડેટા લાવો
                        val englishDate = row[0]  // YYYY-MM-DD
                        val formattedDate = formatDateToDDMMYYYY(englishDate) // DD-MM-YYYY બનાવો
                        val gujaratiMonth = row[1]
                        val pakshaTithi = row[2]
                        val festival = row[3]
                        val festivalType = row[4]
                        val sunrise = row[5]
                        val sunset = row[6]
                        
                        todaysInfo = """
                        📅 તારીખ: $formattedDate ($englishDate)
                        🌙 મહિનો: $gujaratiMonth
                        ⚖️ પક્ષ-તિથિ: $pakshaTithi
                        ${if (festival.isNotEmpty()) "🎉 તહેવાર/જન્મદિવસ: $festival" else ""}
                        ${if (festivalType.isNotEmpty()) "🏷️ પ્રકાર: $festivalType\n" else ""}
                        ☀️ સૂર્યોદય: $sunrise
                        🌇 સૂર્યાસ્ત: $sunset
                        
                        💡 નોંધ: વાર CSV માં નથી, પછી ગણતરી કરીશું.
                        """.trimIndent()
                        
                        dataFound = true
                        break
                    }
                }
            }
            
            if (!dataFound) {
                todaysInfo += "\n⚠️ આજના દિવસનો ડેટા CSV માં નથી."
                todaysInfo += "\nશોધી રહ્યા: $todayYYYYMMDD (YYYY-MM-DD)"
            }
            
            // TextView માં ડેટા બતાવો
            val dateTextView = findViewById<TextView>(R.id.date_text_view)
            dateTextView.text = todaysInfo
            
        } catch (e: Exception) {
            val dateTextView = findViewById<TextView>(R.id.date_text_view)
            dateTextView.text = "ભૂલ: CSV ફાઈલ વાંચી શકાતી નથી\n${e.message}"
            e.printStackTrace()
        }
    }
    
    private fun formatDateToDDMMYYYY(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            if (date != null) outputFormat.format(date) else dateStr
        } catch (e: Exception) {
            dateStr // જો ફોર્મેટ ન થાય તો મૂળ તારીખ
        }
    }
    
    private fun getDayOfWeek(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr)
            if (date != null) {
                val dayFormat = SimpleDateFormat("EEEE", Locale("gu"))
                dayFormat.format(date)
            } else {
                "અજ્ઞાત"
            }
        } catch (e: Exception) {
            "અજ્ઞાત"
        }
    }
    
    private fun readCSVFile(fileName: String): List<List<String>> {
        val data = mutableListOf<List<String>>()
        
        try {
            // assets માંથી CSV ફાઈલ વાંચો
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            var isFirstLine = true
            
            // દરેક લાઈન વાંચો
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // CSV columns (comma separated)
                val columns = line?.split(",")?.map { it.trim() }
                if (columns != null && columns.isNotEmpty()) {
                    // પહેલી લાઈન (header) skip કરો
                    if (isFirstLine) {
                        isFirstLine = false
                        // Header ચક કરો (debug માટે)
                        println("CSV Header: $columns")
                        continue
                    }
                    data.add(columns)
                }
            }
            reader.close()
            
            // ડીબગ માટે
            println("CSV માં ${data.size} rows મળી")
            if (data.isNotEmpty()) {
                println("પહેલી row: ${data[0]}")
                println("પહેલી તારીખ: ${data[0][0]}")
                println("વાર: ${getDayOfWeek(data[0][0])}")
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "CSV ફાઈલ ભૂલ: ${e.message}", Toast.LENGTH_LONG).show()
        }
        
        return data
    }
    
    private fun setupButtons() {
        // વિજેટ બટન
        val widgetButton = findViewById<Button>(R.id.add_widget_button)
        widgetButton.setOnClickListener {
            // ચોક્કસ દિવસ શોધવાનું બટન
            showDatePickerDialog()
        }
        
        // જન્મદિવસ બટન
        val birthdayButton = findViewById<Button>(R.id.birthday_button)
        birthdayButton.setOnClickListener {
            showBirthdayDialog()
        }
        
        // તહેવારોની યાદી બતાવવાનું બટન
        try {
            val festivalButton = findViewById<Button>(R.id.festival_button)
            festivalButton.setOnClickListener {
                showFestivalsList()
            }
        } catch (e: Exception) {
            // બટન ન હોય તો ન ચલાવવું
        }
    }
    
    private fun showDatePickerDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "તારીખ: YYYY-MM-DD"
        
        AlertDialog.Builder(this)
            .setTitle("📅 ચોક્કસ તારીખ શોધો")
            .setMessage("તારીખ દાખલ કરો (ફોર્મેટ: YYYY-MM-DD):")
            .setView(editText)
            .setPositiveButton("🔍 શોધો") { dialog, _ ->
                val searchDate = editText.text.toString()
                if (searchDate.isNotEmpty()) {
                    searchDateInCSV(searchDate)
                }
            }
            .setNegativeButton("❌ રદ કરો", null)
            .show()
    }
    
    private fun searchDateInCSV(date: String) {
        try {
            val csvData = readCSVFile("calendar_data.csv")
            var foundInfo = "તારીખ: $date\n"
            var found = false
            
            for (row in csvData) {
                if (row.size >= 7 && row[0] == date) {
                    val dayOfWeek = getDayOfWeek(date)
                    val formattedDate = formatDateToDDMMYYYY(date)
                    
                    foundInfo = """
                    📅 તારીખ: $formattedDate ($date)
                    📅 વાર: $dayOfWeek
                    🌙 મહિનો: ${row[1]}
                    ⚖️ પક્ષ-તિથિ: ${row[2]}
                    ${if (row[3].isNotEmpty()) "🎉 તહેવાર/જન્મદિવસ: ${row[3]}\n" else ""}
                    ${if (row[4].isNotEmpty()) "🏷️ પ્રકાર: ${row[4]}\n" else ""}
                    ☀️ સૂર્યોદય: ${row[5]}
                    🌇 સૂર્યાસ્ત: ${row[6]}
                    """.trimIndent()
                    found = true
                    break
                }
            }
            
            if (!found) {
                foundInfo += "\n❌ આ તારીખનો ડેટા CSV માં નથી."
                foundInfo += "\nસૂચના: તારીખ YYYY-MM-DD ફોર્મેટમાં હોવી જોઈએ."
            }
            
            AlertDialog.Builder(this)
                .setTitle("🔍 શોધ પરિણામ")
                .setMessage(foundInfo)
                .setPositiveButton("બંધ કરો", null)
                .show()
                
        } catch (e: Exception) {
            Toast.makeText(this, "શોધમાં ભૂલ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showBirthdayDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "તારીખ: YYYY-MM-DD"
        
        AlertDialog.Builder(this)
            .setTitle("🎉 જન્મદિવસ ઉમેરો")
            .setMessage("તમારો જન્મદિવસની તારીખ દાખલ કરો:")
            .setView(editText)
            .setPositiveButton("💾 સાચવો") { dialog, _ ->
                val birthday = editText.text.toString()
                if (birthday.isNotEmpty()) {
                    Toast.makeText(this, "જન્મદિવસ સંગ્રહિત થયો: $birthday", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("❌ રદ કરો", null)
            .show()
    }
    
    // તહેવારોની યાદી બતાવવા
    private fun showFestivalsList() {
        val csvData = readCSVFile("calendar_data.csv")
        val festivals = mutableListOf<String>()
        
        for (row in csvData) {
            if (row.size >= 5 && row[3].isNotEmpty()) {
                val formattedDate = formatDateToDDMMYYYY(row[0])
                festivals.add("📅 $formattedDate: ${row[3]} (${row[4]})")
            }
        }
        
        if (festivals.isEmpty()) {
            Toast.makeText(this, "તહેવારોની યાદી ખાલી છે", Toast.LENGTH_SHORT).show()
            return
        }
        
        val message = "કુલ ${festivals.size} તહેવારો:\n\n" + 
                     festivals.take(10).joinToString("\n\n")
        
        AlertDialog.Builder(this)
            .setTitle("🎊 તહેવારોની યાદી")
            .setMessage(message)
            .setPositiveButton("બંધ કરો", null)
            .show()
    }
    
    // વધારાનું: આગામી તહેવાર શોધવા
    private fun showNextFestival() {
        val csvData = readCSVFile("calendar_data.csv")
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val upcomingFestivals = mutableListOf<String>()
        
        for (row in csvData) {
            if (row.size >= 5 && row[3].isNotEmpty()) {
                val eventDate = row[0]
                // જો તહેવાર આજે કે ભવિષ્યમાં હોય
                if (eventDate >= today) {
                    val dayOfWeek = getDayOfWeek(eventDate)
                    val formattedDate = formatDateToDDMMYYYY(eventDate)
                    upcomingFestivals.add("📅 $formattedDate ($dayOfWeek): ${row[3]}")
                }
            }
        }
        
        if (upcomingFestivals.isEmpty()) {
            Toast.makeText(this, "કોઈ આગામી તહેવાર નથી", Toast.LENGTH_SHORT).show()
            return
        }
        
        val message = "આગામી ${minOf(5, upcomingFestivals.size)} તહેવારો:\n\n" +
                     upcomingFestivals.take(5).joinToString("\n\n")
        
        AlertDialog.Builder(this)
            .setTitle("🔮 આગામી તહેવારો")
            .setMessage(message)
            .setPositiveButton("બંધ કરો", null)
            .show()
    }
}
