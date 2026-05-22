package com.uade.costaapp.data.repository

import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.domain.repository.PropertyRepository
import com.uade.costaapp.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow
import java.io.IOException
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
                    val entities = dtos.map { mapper.dtoToEntity(it) }
                    dao.upsertAll(entities)
                }
            }
        } catch (e: IOException) {
            // Error de red manejado de forma transparente.
            // Si la llamada falla, la UI seguirá reaccionando a los datos existentes en Room (Offline-first).
        } catch (e: Exception) {
            // Otros errores, podríamos loguear con Timber como menciona la estrategia.
        }
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
}
