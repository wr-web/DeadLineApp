package com.example.deadline.holder

import android.view.View
import android.widget.TextView
import com.example.deadline.R
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.headerTextView)
}