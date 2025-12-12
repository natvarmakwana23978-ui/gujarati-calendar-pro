class MainActivity : AppCompatActivity() {
    
    // UI એલિમેન્ટ્સ
    private lateinit var tvTithi: TextView
    private lateinit var tvRashi: TextView
    private lateinit var tvMonth: TextView
    private lateinit var tvEvent: TextView
    private lateinit var tvSunrise: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // UI એલિમેન્ટ્સ શોધો
        tvTithi = findViewById(R.id.tv_tithi)  // તમારા XMLમાં આ ID છે તે મુજબ
        tvRashi = findViewById(R.id.tv_rashi)
        tvMonth = findViewById(R.id.tv_month)
        tvEvent = findViewById(R.id.tv_event)
        tvSunrise = findViewById(R.id.tv_sunrise)
        
        // CSV લોડર બનાવો
        val csvLoader = CsvLoader(this)
        
        // CSV ડેટા UI માં દર્શાવો
        displayCsvData(csvLoader)
    }
    
    private fun displayCsvData(csvLoader: CsvLoader) {
        // CSVમાંથી આજનો ડેટા લાવો
        val todayData = csvLoader.getTodayPanchang()
        
        if (todayData != null) {
            // CSV ડેટા દર્શાવો
            tvTithi.text = "🌙 તિથિ: ${todayData.tithiName}"
            tvMonth.text = "🗓️ મહિનો: ${todayData.month}"
            tvSunrise.text = "☀️ સૂર્યોદય: ${todayData.sunrise.substring(0, 5)}"
            
            // તહેવાર (જો હોય)
            if (todayData.eventName.isNotBlank()) {
                tvEvent.text = "🎉 ${todayData.eventName}"
                tvEvent.visibility = View.VISIBLE
            } else {
                tvEvent.visibility = View.GONE
            }
            
            // રાશિ (તમારી CSVમાં નથી, તેથી મૂળભૂત)
            tvRashi.text = "✨ રાશિ: મેષ"  // હાર્ડકોડેડ (આગળ CSVમાં ઉમેરશું)
            
            Log.d("UI_UPDATE", "CSV ડેટા દર્શાવ્યું: ${todayData.tithiName}")
            
        } else {
            // CSV ડેટા ન મળે તો હાર્ડકોડેડ
            tvTithi.text = "🌙 તિથિ: પ્રતિપ્રદા (CSV ન મળ્યું)"
            tvRashi.text = "✨ રાશિ: મેષ"
            tvMonth.text = "🗓️ મહિનો: ચૈત્ર"
            tvSunrise.text = "☀️ સૂર્યોદય: 06:00"
            
            Log.e("UI_UPDATE", "CSV ડેટા ન મળ્યો, હાર્ડકોડેડ દર્શાવ્યું")
        }
    }
}
