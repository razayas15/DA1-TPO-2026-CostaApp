package com.uade.costaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Upsert
    suspend fun upsertAll(properties: List<PropertyEntity>)

    @Query("UPDATE properties SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM properties ORDER BY lastUpdated DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyById(id: String): Flow<PropertyEntity?>
}
