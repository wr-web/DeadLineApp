package com.example.deadline.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.deadline.ChDataActivity
import com.example.deadline.R
import com.example.deadline.adapter.TripListViewAdapter
import com.example.deadline.database.DateDatabase
import com.example.deadline.databinding.FragmentCalendarBinding
import com.example.deadline.holder.MonthViewContainer
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: LocalDate? = null
    lateinit var year:String
    lateinit var month:String
    lateinit var gday:String
    var dbSelectedDates:MutableList<LocalDate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root = binding.root
        val fab = binding.fabAdd
        val calendar:Calendar = Calendar.getInstance()
        year  = calendar.get(Calendar.YEAR).toString()
        month = (calendar.get(Calendar.MONTH)+1).toString()
        gday   = calendar.get(Calendar.DAY_OF_MONTH).toString()
        selectedDate = LocalDate.of(year.toInt(),month.toInt(),gday.toInt())
        fab.setOnClickListener {
            sendMessage(it,"DDL","${year}-${month}-${gday} 12-00","")
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListView()
        showSelectedDates()
        val calendarView = binding.calendarView
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.calendarDayText)
            // Will be set when this container is bound
            lateinit var day: CalendarDay
            init {
                view.setOnClickListener {
                    // Check the day owner as we do not want to select in or out dates.
                    if (day.owner == DayOwner.THIS_MONTH) {
                        // Keep a reference to any previous selection
                        // in case we overwrite it and need to reload it.
                        val currentSelection = selectedDate
                        if (currentSelection == day.date) {
                            // If the user clicks the same date, clear selection.
                            selectedDate = null
                            // Reload this date so the dayBinder is called
                            // and we can REMOVE the selection background.
                            calendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = day.date
                            // Reload the newly selected date so the dayBinder is
                            // called and we can ADD the selection background.
                            calendarView.notifyDateChanged(day.date)
                            if(currentSelection != null) {
                                // We need to also reload the previously selected
                                // date so we can REMOVE the selection background.
                                calendarView.notifyDateChanged(currentSelection)
                            }
                        }
                    }
                }
            }
        }
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    // Show the month dates. Remember that views are recycled!
                    textView.visibility = View.VISIBLE
                    if (day.date == selectedDate) {
                        // If this is the selected date, show a round background and change the text color.
                        textView.setTextColor(Color.WHITE)
                        textView.setBackgroundResource(R.drawable.ic_baseline_fiber_manual_record_24)
                        year = day.date.year.toString()
                        month = day.date.monthValue.toString()
                        gday = day.date.dayOfMonth.toString()
                        setUpListView()
                    }else if(day.date in dbSelectedDates){
                        textView.setTextColor(Color.WHITE)
                        textView.setBackgroundResource(R.drawable.ic_green_backround)
                    } else {
                        // If this is NOT the selected date, remove the background and reset the text color.
                        textView.setTextColor(Color.BLACK)
                        textView.background = null
                    }
                } else {
                    // Hide in and out dates
                    textView.visibility = View.INVISIBLE
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.headerTextView)
        }
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }
    }
    override fun onResume() {
        super.onResume()
        showSelectedDates()
        binding.calendarView.notifyCalendarChanged()
    }
    fun showSelectedDates(){
        val dbHelper = DateDatabase(this.requireContext())
        val db = dbHelper.writableDatabase
        val target = ""
        var ddlList = dbHelper.searchData(db,target)
        var tmpdbSelectedDates:MutableList<LocalDate> = mutableListOf()
        for(item in ddlList){
            var list_time = item.date.split("-"," ")
            tmpdbSelectedDates.add(LocalDate.of(list_time[0].toInt(),list_time[1].toInt(),list_time[2].toInt()))
        }
        dbSelectedDates = tmpdbSelectedDates
    }

    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            return rhs + lhs
        }
        return daysOfWeek
    }
    fun setUpListView(){
        val dbHelper = DateDatabase(this.requireContext())
        val db = dbHelper.writableDatabase
        val target = "${year}-${month}-${gday}"
        var ddlList = dbHelper.searchDate(db,target)
        val listView = binding.listView
        var adapter = TripListViewAdapter(this.requireContext(),ddlList)
        listView.adapter = adapter
        listView.deferNotifyDataSetChanged()
        listView.setOnItemClickListener { parent, view, position, id ->
            activity.let{
                val intent = Intent(it, ChDataActivity::class.java).apply {
                    putExtra(PlaceholderFragment.EXTRA_MESSAGE_DDL,ddlList[position].ddl)
                    putExtra(PlaceholderFragment.EXTRA_MESSAGE_DATE,ddlList[position].date)
                    putExtra(PlaceholderFragment.EXTRA_MESSAGE_DISC,ddlList[position].disc)
                }
                it?.startActivity(intent)
            }
        }
    }
    fun sendMessage(view:View,ddl:String,date:String,disc:String){
        val intent = Intent(this.requireContext(), ChDataActivity::class.java).apply{
            putExtra(PlaceholderFragment.EXTRA_MESSAGE_DDL,ddl)
            putExtra(PlaceholderFragment.EXTRA_MESSAGE_DATE,date)
            putExtra(PlaceholderFragment.EXTRA_MESSAGE_DISC,disc)
        }
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}