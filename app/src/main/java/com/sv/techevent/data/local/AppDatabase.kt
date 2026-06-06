package com.sv.techevent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sv.techevent.data.local.dao.EventDao
import com.sv.techevent.data.local.entity.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}