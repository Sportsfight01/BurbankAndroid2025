package com.dmss.burbankapp.data.local.entity

import android.content.Context
import androidx.room.Room
import com.dmss.burbankapp.data.local.AppDatabase
import com.dmss.burbankapp.utils.DATABASE_NAME

object DatabaseBuilder {

    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

}