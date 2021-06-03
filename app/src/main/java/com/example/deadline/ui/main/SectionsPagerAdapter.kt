@file:Suppress("DEPRECATION")

package com.example.deadline.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentManager
import com.example.deadline.R
import com.example.deadline.fragment.CalendarFragment
import com.example.deadline.fragment.PlaceholderFragment
import com.example.deadline.fragment.SearchFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        when(position){
            0 -> return PlaceholderFragment.newInstance()
            1 -> return CalendarFragment.newInstance()
            2 -> return SearchFragment.newInstance()
            else -> return PlaceholderFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 3
    }
}