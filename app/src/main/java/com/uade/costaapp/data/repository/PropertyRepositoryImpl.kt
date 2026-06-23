package com.uade.costaapp.data.repository

import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.domain.repository.PropertyRepository
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val api: CostaAppApiService,
    private val dao: PropertyDao,
    private val mapper: PropertyMapper,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PropertyRepository {

    override fun getAllProperties(): Flow<List<PropertyEntity>> {
        return dao.getAllProperties()
    }

    override suspend fun refreshProperties() {
        // Ejecuta directo. Como la UI recolecta de Flow independientemente,
        // esto actúa como "background silencioso" natural sin crear scopes desconectados
        // que pierden la excepción.
        fetchFromNetworkAndUpdate()
    }

    private suspend fun fetchFromNetworkAndUpdate() {
        try {
            val response = api.getProperties()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val existingProps = dao.getAllProperties().first().associateBy { it.id }
                    
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
                    dao.upsertAll(entities)
                }
            } else {
                throw Exception("Error de red")
            }
        } catch (e: Exception) {
            throw e // Propagamos para mostrar UI offline
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

        // Sync remoto con Firestore
        auth.currentUser?.uid?.let { userId ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRef = firestore.collection("users").document(userId)
                    if (isFavorite) {
                        userRef.update("favorite_property_ids", FieldValue.arrayUnion(id))
                    } else {
                        userRef.update("favorite_property_ids", FieldValue.arrayRemove(id))
                    }
                } catch (e: Exception) {
                    // Fallo silencioso offline
                }
            }
        }
    }

    override fun getFavoritesCount(): Flow<Int> = dao.getFavoritesCount()
    override fun getViewedCount(): Flow<Int> = dao.getViewedCount()
}
