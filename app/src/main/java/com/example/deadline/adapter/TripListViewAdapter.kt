package com.example.deadline.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.deadline.R
import com.example.deadline.model.ListModel
import java.text.SimpleDateFormat
import java.util.*

class TripListViewAdapter(private val context:Context, private val arrayList:MutableList<ListModel>): BaseAdapter(){
    lateinit var serialNum:TextView
    lateinit var ddlNmae:TextView
    lateinit var date:TextView
    lateinit var disc:TextView
    lateinit var timeLeftView:TextView
    private val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return arrayList.count()
    }
    override fun getItem(position: Int): Any {
        return arrayList[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_row,parent,false)
        serialNum = rowView.findViewById(R.id.serialNumber)
        ddlNmae = rowView.findViewById(R.id.ddlName)
        date = rowView.findViewById(R.id.date)
        disc = rowView.findViewById(R.id.disc)
        timeLeftView = rowView.findViewById(R.id.time_left)

        var ddl_date_disc = getItem(position) as ListModel
        serialNum.text = (position+1).toString()
        ddlNmae.text = ddl_date_disc.ddl
        date.text = ddl_date_disc.date
        disc.text = ddl_date_disc.disc

        val list_time = ddl_date_disc.date.split("-"," ")
        var year = list_time[0]
        var month = list_time[1]
        var day = list_time[2]
        var hour = list_time[3]
        var min  = list_time[4]


        val calendar: Calendar = Calendar.getInstance()
        var nyear  = calendar.get(Calendar.YEAR).toString()
        var nmonth = (calendar.get(Calendar.MONTH)+1).toString()
        var nday   = calendar.get(Calendar.DAY_OF_MONTH).toString()
        var nhour  = calendar.get(Calendar.HOUR_OF_DAY).toString()
        var nmin   = calendar.get(Calendar.MINUTE).toString()
        var endTime = "$year-$month-$day $hour:$min:00"
        var currentTIme = "$nyear-$nmonth-$nday $nhour:$nmin:00"

        val (str1,str2) = time_left(currentTIme,endTime)
        timeLeftView.text = str2 + "\n" + str1
        return rowView
    }

    fun time_left(currentTime:String,endTime:String):Pair<String,String>{
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val curDate = df.parse(currentTime)
        val endDate = df.parse(endTime)
        var diff = endDate.time - curDate.time
        val day = diff / (1000 * 60 * 60 * 24)
        val hour = diff / (60 * 60 * 1000) - day * 24
        val min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
        if(diff < 0){
            return Pair("PASSED","")
        }else if(day >= 1){
            return Pair("Days Left",day.toString())
        }else if(hour>=1){
            return Pair("Hours Left",hour.toString())
        }else if(min >= 1){
            return Pair("MINUTES Left",min.toString())
        }else{
            return Pair("HURRY UP","")
        }
    }
}