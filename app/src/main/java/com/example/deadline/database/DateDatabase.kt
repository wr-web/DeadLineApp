package com.example.deadline.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.deadline.model.ListModel
import com.example.deadline.model.Table
import kotlinx.coroutines.selects.select
import kotlin.math.min

class DateDatabase(context:Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    private val context = context
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    fun partionZero(strSec:CharSequence,tarInt:Int):String{
        if(strSec.length != tarInt){
            if(strSec.length > tarInt){
                return strSec.substring(0,tarInt)
            }else{
                return "0".repeat(tarInt-strSec.length) + strSec
            }
        }
        return strSec.toString()
    }
    fun toZeroFormat(date:String):String{
        val dateList: MutableList<String> = date.split("-"," ").toMutableList()
        if(dateList.size >= 3){
            dateList[0] = partionZero(dateList[0],4)
            dateList[1] = partionZero(dateList[1],2)
            dateList[2] = partionZero(dateList[2],2)
            if(dateList.size == 5){
                dateList[3] = partionZero(dateList[3],2)
                dateList[4] = partionZero(dateList[4],2)
                return "${dateList[0]}-${dateList[1]}-${dateList[2]} ${dateList[3]}-${dateList[4]}"
            }
            return "${dateList[0]}-${dateList[1]}-${dateList[2]}"
        }
        return ""
    }
    fun fromZeroFormat(str:String):String{
        val strList = str.split("-"," ")
        val yearStr = strList[0].toInt().toString()
        val monthStr = strList[1].toInt().toString()
        val dayStr = strList[2].toInt().toString()
        val hourStr = strList[3].toInt().toString()
        val minStr = strList[4].toInt().toString()
        return "${yearStr}-${monthStr}-${dayStr} ${hourStr}-${minStr}"
    }

    fun addData(db:SQLiteDatabase?,ddl:String,date:String,disc:String){

        val date = toZeroFormat(date)
        var values:ContentValues = ContentValues().apply {
            put(Table.ColName.COLUM_NAME_DDL,ddl)
            put(Table.ColName.COLUM_NAME_DATE,date)
            put(Table.ColName.COLUM_NAME_DISC,disc)
        }
        if(isExist(db,ddl)){
            db?.insert(Table.TABLE_NAME,null,values)
        }else{
            val selection = "${Table.ColName.COLUM_NAME_DDL} LIKE ?"
            val selectionArgs = arrayOf(ddl)
            db?.update(Table.TABLE_NAME,values,selection,selectionArgs)
            Toast.makeText(context,"Event has been updated",Toast.LENGTH_SHORT).show()
        }
    }
    fun delData(db:SQLiteDatabase?,ddl:String){
        val selection = "${Table.ColName.COLUM_NAME_DDL} LIKE ?"
        val selectionArgs = arrayOf(ddl)
        db?.delete(Table.TABLE_NAME, selection, selectionArgs)
    }
    fun isExist(db: SQLiteDatabase?,ddl: String):Boolean{
        val projection = arrayOf(Table.ColName.COLUM_NAME_DDL)
        val selection = "${Table.ColName.COLUM_NAME_DDL} = ?"
        val selectionArgs = arrayOf(ddl)
        val cursor = db?.query(Table.TABLE_NAME,projection,selection,selectionArgs,null,null,null)
        return cursor?.count == 0
    }
    fun searchData(db:SQLiteDatabase?,ddl:String):MutableList<ListModel>{
        var target = "%"+ddl+"%"
        if (ddl == ""){
            target = "%"
        }
        val selected = "${Table.ColName.COLUM_NAME_DDL} LIKE ?"
        var cursor:Cursor = db!!.query(
            Table.TABLE_NAME, null, selected, arrayOf(target), null, null,
            Table.ColName.COLUM_NAME_DATE + " ASC", null)
        var item:ListModel
        var outItems = mutableListOf<ListModel>()
        with(cursor){
            while(moveToNext()){
                item = ListModel(getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DDL)),
                    fromZeroFormat(getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DATE))),
                    getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DISC)))
                outItems.add(item)
            }
        }
        return outItems
    }
    fun searchDate(db:SQLiteDatabase?,partionDate:String):MutableList<ListModel>{
        var target = toZeroFormat(partionDate)
        target = target +" %"
        val selected = "${Table.ColName.COLUM_NAME_DATE} LIKE ?"
        val cursor:Cursor = db!!.query(
            Table.TABLE_NAME, null, selected, arrayOf(target), null, null,
            Table.ColName.COLUM_NAME_DATE + " ASC", null)
        var item:ListModel
        var outItems = mutableListOf<ListModel>()
        with(cursor){
            while(moveToNext()){
                item = ListModel(getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DDL)),
                    fromZeroFormat(getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DATE))),
                    getString(getColumnIndexOrThrow(Table.ColName.COLUM_NAME_DISC)))
                outItems.add(item)
            }
        }
        return outItems
    }

    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "DateDatabase.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${Table.TABLE_NAME} (" +
                    "${Table.ColName.COLUM_NAME_DDL} TEXT PRIMARY KEY," +
                    "${Table.ColName.COLUM_NAME_DATE} TEXT,"            +
                    "${Table.ColName.COLUM_NAME_DISC} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Table.TABLE_NAME}"
    }

}