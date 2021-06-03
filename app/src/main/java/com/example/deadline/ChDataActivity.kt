package com.example.deadline

import android.content.Context
import android.content.Intent
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.provider.CalendarContract
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.deadline.database.DateDatabase
import com.example.deadline.databinding.ActivityChDataBinding


class ChDataActivity : AppCompatActivity() {
    lateinit var ddl:String
    lateinit var date:String
    lateinit var disc:String
    private lateinit var binding: ActivityChDataBinding
    lateinit var year_edit:EditText
    lateinit var month_eidt:EditText
    lateinit var day_edit:EditText
    lateinit var hour_edit:EditText
    lateinit var min_edit:EditText
    lateinit var thing_edit:EditText
    lateinit var disc_edit:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        year_edit  = binding.yearEditText
        month_eidt = binding.monthEditText
        day_edit   = binding.dayEditText
        hour_edit  = binding.hourEditText
        min_edit   = binding.minuteEditText
        thing_edit = binding.thingEdit
        disc_edit  = binding.discEidt

        ddl = intent.getStringExtra(EXTRA_MESSAGE_DDL).toString()
        date = intent.getStringExtra(EXTRA_MESSAGE_DATE).toString()
        disc = intent.getStringExtra(EXTRA_MESSAGE_DISC).toString()

        var list_date = date?.split("-"," ")
        year_edit.setText(list_date[0])
        month_eidt.setText(list_date[1])
        day_edit.setText(list_date[2])
        hour_edit.setText(list_date[3])
        min_edit.setText(list_date[4])
        thing_edit.setText(ddl)
        disc_edit.setText(disc)

        val fab_add = binding.fabAdd
        val fab_del = binding.fabDestory
        val fab_ala = binding.fabAlarm

        fab_add.setOnClickListener {
            add_click()
            super.onBackPressed()
        }
        fab_del.setOnClickListener {
            del_click()
            super.onBackPressed()
        }
        fab_ala.setOnClickListener {
            ala_click()
        }
    }

    fun ala_click(){
        //TODO ALARM
        add_click()
        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = "vnd.android.cursor.item/event"

        val id = "123"
        val year = year_edit.text.toString().toInt()
        val month = month_eidt.text.toString().toInt()-1
        val dayOfMonth = day_edit.text.toString().toInt()
        val fechaSeleccionada = GregorianCalendar(year, month, dayOfMonth)
        intent.putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            fechaSeleccionada.getTimeInMillis()
        )
        intent.putExtra(
            CalendarContract.EXTRA_EVENT_END_TIME,
            fechaSeleccionada.getTimeInMillis()
        )
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
        intent.putExtra(CalendarContract.EXTRA_EVENT_ID, id)
        //This is Information about Calender Event.
        intent.putExtra(CalendarContract.Events.TITLE, binding.thingEdit.text.toString())
        intent.putExtra(CalendarContract.Events.DESCRIPTION,binding.discEidt.text.toString())
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,"")
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY")
        //This is Information about Reminders.
        intent.putExtra(CalendarContract.Reminders.EVENT_ID, id)
        intent.putExtra(CalendarContract.Reminders.MINUTES, binding.reminderTimeEdit.text.toString().toInt())
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        startActivity(intent)
    }


    fun add_click(){
        val year  = binding.yearEditText.text.toString()
        val month = binding.monthEditText.text.toString()
        val day   = binding.dayEditText.text.toString()
        val hour  = binding.hourEditText.text.toString()
        val min   = binding.minuteEditText.text.toString()
        val date = "${year}-${month}-${day} ${hour}-${min}"
        val ddl_name:String = binding.thingEdit.text.toString()
        val disc:String = binding.discEidt.text.toString()

        val dbHelper = DateDatabase(this.applicationContext)
        val db = dbHelper.writableDatabase
        dbHelper.addData(db,ddl_name,date,disc)
    }
    fun del_click(){
        val ddl_name:String = binding.thingEdit.text.toString()
        val dbHelper = DateDatabase(this.applicationContext)
        val db = dbHelper.writableDatabase
        dbHelper.delData(db,ddl_name)
    }

    companion object{
        const val EXTRA_MESSAGE_DDL = "DDL"
        const val EXTRA_MESSAGE_DATE = "DATE"
        const val EXTRA_MESSAGE_DISC = "DISC"
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}