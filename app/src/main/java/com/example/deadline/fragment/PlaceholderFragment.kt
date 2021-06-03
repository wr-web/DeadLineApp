package com.example.deadline.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deadline.ChDataActivity
import com.example.deadline.adapter.TripListViewAdapter
import com.example.deadline.database.DateDatabase
import com.example.deadline.databinding.FragmentMainBinding
import java.util.*

class PlaceholderFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listView:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //listView
        listView = binding.listView
        updateAdapter()
        //fab
        val fab = binding.fabAdd
        fab.setOnClickListener {
            activity.let{
                val intent = Intent(it,ChDataActivity::class.java).apply {
                    val calendar= Calendar.getInstance();
                    val year    = calendar.get(Calendar.YEAR).toString()
                    val month   =(calendar.get(Calendar.MONTH)+1).toString()
                    val day     = calendar.get(Calendar.DAY_OF_MONTH).toString()
                    val hour    = calendar.get(Calendar.HOUR_OF_DAY).toString()
                    val min     = calendar.get(Calendar.MINUTE).toString()
                    val date    = "${year}-${month}-${day} ${hour}-${min}"
                    putExtra(EXTRA_MESSAGE_DDL,"DDL")
                    putExtra(EXTRA_MESSAGE_DATE,date)
                    putExtra(EXTRA_MESSAGE_DISC,"")
                }
                it?.startActivity(intent)
            }
        }
    }

    fun updateAdapter(){
        val dbHelper = DateDatabase(this.requireContext())
        val db = dbHelper.writableDatabase
        var ddlList = dbHelper.searchData(db,"")
        var adapter = TripListViewAdapter(this.requireContext(),ddlList)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            activity.let{
                val intent = Intent(it,ChDataActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE_DDL,ddlList[position].ddl)
                    putExtra(EXTRA_MESSAGE_DATE,ddlList[position].date)
                    putExtra(EXTRA_MESSAGE_DISC,ddlList[position].disc)
                }
                it?.startActivity(intent)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val listView = binding.listView
        updateAdapter()
        listView.deferNotifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val EXTRA_MESSAGE_DDL = "DDL"
        const val EXTRA_MESSAGE_DATE = "DATE"
        const val EXTRA_MESSAGE_DISC = "DISC"
        @JvmStatic
        fun newInstance(): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}