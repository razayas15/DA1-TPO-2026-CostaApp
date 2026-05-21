package com.uade.costaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.local.entity.PropertyEntity

@Database(entities = [PropertyEntity::class], version = 1, exportSchema = false)
abstract class CostaAppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
}
