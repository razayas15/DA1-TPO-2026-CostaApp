package com.uade.costaapp.domain.repository

import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getAllProperties(): Flow<List<PropertyEntity>>
    fun getFavorites(): Flow<List<PropertyEntity>>
    suspend fun getPropertyById(id: String): PropertyEntity?
    suspend fun refreshProperties()
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}
