package com.uade.costaapp.data.repository

import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.domain.repository.PropertyRepository
import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val api: CostaAppApiService,
    private val dao: PropertyDao,
    private val mapper: PropertyMapper
) : PropertyRepository {

    override fun getAllProperties(): Flow<List<PropertyEntity>> {
        return dao.getAllProperties()
    }

    override suspend fun refreshProperties() {
        try {
            val response = api.getProperties()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    // 1. Obtenemos el estado local actual para no pisar isFavorite y lastViewedAt
                    val existingProps = dao.getAllProperties().first().associateBy { it.id }
                    
                    // 2. Mapeamos respetando el estado local
                    val entities = dtos.map { dto ->
                        val mapped = mapper.dtoToEntity(dto)
                        val existing = existingProps[mapped.id]
                        if (existing != null) {
                            mapped.copy(
                                isFavorite = existing.isFavorite,
                                lastViewedAt = existing.lastViewedAt
                            )
                        } else {
                            mapped
                        }
                    }
                    // 3. UpsertAll puro en Room. Room es ahora el SSOT absoluto.
                    dao.upsertAll(entities)
                }
            }
        } catch (e: Exception) {
            // Falla de red: manejado silenciosamente, Room sigue sirviendo los datos offline
        }
    }

    override suspend fun markAsViewed(id: String) {
        dao.markAsViewed(id, System.currentTimeMillis())
    }

    override fun getFavorites(): Flow<List<PropertyEntity>> {
        return dao.getFavorites()
    }

    override fun getPropertyById(id: String): Flow<PropertyEntity?> {
        return dao.getPropertyById(id)
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    override fun getFavoritesCount(): Flow<Int> = dao.getFavoritesCount()
    override fun getViewedCount(): Flow<Int> = dao.getViewedCount()
}
