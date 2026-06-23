package com.uade.costaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.local.entity.PropertyEntity

@Database(entities = [PropertyEntity::class], version = 3, exportSchema = false)
@androidx.room.TypeConverters(Converters::class)
abstract class CostaAppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
}
