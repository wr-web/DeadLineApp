package com.example.deadline.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.deadline.ChDataActivity
import com.example.deadline.adapter.TripListViewAdapter
import com.example.deadline.database.DateDatabase
import com.example.deadline.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init database
        val dbHelper = DateDatabase(this.requireContext())
        val db = dbHelper.writableDatabase
        //get data from EditText
        val editText = binding.editText
        val button   = binding.buttonSearch
        val listView = binding.listView
        button.setOnClickListener {
            val target = "%"+editText.text.toString()+"%"
            var ddlList = dbHelper.searchData(db,target)
            var adapter = TripListViewAdapter(this.requireContext(),ddlList)
            listView.adapter = adapter
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
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}