package com.ygorcesar.jamdroidfirechat.data.repository.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference

@Database(entities = [(ChatReference::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatReferenceDao(): ChatReferenceDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "AppDatabase.db")
                            .allowMainThreadQueries()
                            .build()
                }
                return INSTANCE!!
            }
        }
    }
}