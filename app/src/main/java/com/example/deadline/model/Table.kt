package com.example.deadline.model

import android.provider.BaseColumns

object Table {
    const val TABLE_NAME = "ddl_data"
    object ColName:BaseColumns{
        const val COLUM_NAME_DDL = "ddl"
        const val COLUM_NAME_DATE = "date"
        const val COLUM_NAME_DISC = "dicribtion"
    }
}