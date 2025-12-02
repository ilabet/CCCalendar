package com.example.newcal
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var etEventTitle: EditText
    private lateinit var btnAddEvent: Button
    private lateinit var tvEvents: TextView

    // Store events in memory: "yyyy-MM-dd" -> list of event titles
    private val eventsByDate: MutableMap<String, MutableList<String>> = mutableMapOf()

    // Track currently selected date as string key
    private var selectedDateKey: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        etEventTitle = findViewById(R.id.etEventTitle)
        btnAddEvent = findViewById(R.id.btnAddEvent)
        tvEvents = findViewById(R.id.tvEvents)

        // Initialize with today's date
        val initialYearMonthDay = getYMDFromMillis(calendarView.date)
        selectedDateKey = toKey(initialYearMonthDay.year, initialYearMonthDay.month, initialYearMonthDay.day)
        updateSelectedDateText(initialYearMonthDay.year, initialYearMonthDay.month, initialYearMonthDay.day)
        updateEventsText()

        // When user picks a date on the calendar
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // month from CalendarView is 0-based; keep that in mind in conversions
            selectedDateKey = toKey(year, month + 1, dayOfMonth)
            updateSelectedDateText(year, month + 1, dayOfMonth)
            updateEventsText()
        }

        // When user taps "Add Event"
        btnAddEvent.setOnClickListener {
            val title = etEventTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Enter an event title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val list = eventsByDate.getOrPut(selectedDateKey) { mutableListOf() }
            list.add(title)
            etEventTitle.text.clear()
            updateEventsText()
        }
    }

    private fun updateSelectedDateText(year: Int, month: Int, day: Int) {
        val text = String.format(Locale.getDefault(),
            "Selected Date: %02d/%02d/%04d", month, day, year)
        tvSelectedDate.text = text
    }

    private fun updateEventsText() {
        val list = eventsByDate[selectedDateKey]
        tvEvents.text = if (list.isNullOrEmpty()) {
            "No events yet"
        } else {
            "Events:\n" + list.joinToString(separator = "\n") { "- $it" }
        }
    }

    // Helper: make a simple yyyy-MM-dd key
    private fun toKey(year: Int, month: Int, day: Int): String {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    // Helper: get year/month/day from milliseconds (for initial date)
    private data class YMD(val year: Int, val month: Int, val day: Int)

    private fun getYMDFromMillis(millis: Long): YMD {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = millis
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1 // convert to 1-based
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return YMD(y, m, d)
    }
}