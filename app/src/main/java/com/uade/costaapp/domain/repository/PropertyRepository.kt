package com.uade.costaapp.domain.repository

import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getAllProperties(): Flow<List<PropertyEntity>>
    fun getFavorites(): Flow<List<PropertyEntity>>
    fun getPropertyById(id: String): Flow<PropertyEntity?>
    suspend fun refreshProperties()
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
    suspend fun markAsViewed(id: String)
}
