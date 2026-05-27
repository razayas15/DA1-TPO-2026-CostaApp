package com.uade.costaapp.data.repository

import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.domain.repository.PropertyRepository
import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.io.IOException
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val api: CostaAppApiService,
    private val dao: PropertyDao,
    private val mapper: PropertyMapper
) : PropertyRepository {

    private val inMemoryCache = MutableStateFlow<List<PropertyEntity>>(emptyList())

    override fun getAllProperties(): Flow<List<PropertyEntity>> {
        return combine(inMemoryCache, dao.getAllProperties()) { memoryList, roomList ->
            if (memoryList.isEmpty()) {
                roomList
            } else {
                val roomMap = roomList.associateBy { it.id }
                memoryList.map { memProp ->
                    roomMap[memProp.id] ?: memProp
                }
            }
        }
    }

    override suspend fun refreshProperties() {
        try {
            val response = api.getProperties()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val entities = dtos.map { mapper.dtoToEntity(it) }
                    inMemoryCache.value = entities
                    dao.cleanupCache()
                }
            }
        } catch (e: Exception) {
            // Manejado silenciosamente, se mostrará Room
        }
    }

    override suspend fun markAsViewed(id: String) {
        val memProp = inMemoryCache.value.find { it.id == id }
        if (memProp != null) {
            dao.upsertAll(listOf(memProp.copy(lastViewedAt = System.currentTimeMillis())))
        } else {
            dao.markAsViewed(id, System.currentTimeMillis())
        }
    }

    override fun getFavorites(): Flow<List<PropertyEntity>> {
        return dao.getFavorites()
    }

    override fun getPropertyById(id: String): Flow<PropertyEntity?> {
        return combine(inMemoryCache, dao.getPropertyById(id)) { memoryList, roomProp ->
            roomProp ?: memoryList.find { it.id == id }
        }
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        val memProp = inMemoryCache.value.find { it.id == id }
        if (memProp != null) {
            dao.upsertAll(listOf(memProp.copy(isFavorite = isFavorite)))
        } else {
            dao.updateFavorite(id, isFavorite)
        }
    }

    override fun getFavoritesCount(): Flow<Int> = dao.getFavoritesCount()
    override fun getViewedCount(): Flow<Int> = dao.getViewedCount()
}
