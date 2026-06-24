package com.uade.costaapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.AiService
import com.uade.costaapp.data.remote.CostaAppApiService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PropertyRepositoryImplTest {

    private val api: CostaAppApiService = mockk()
    private val dao: PropertyDao = mockk(relaxed = true)
    private val mapper: PropertyMapper = mockk()
    private val auth: FirebaseAuth = mockk(relaxed = true)
    private val firestore: FirebaseFirestore = mockk(relaxed = true)
    private val aiService: AiService = mockk()
    private val context: Context = mockk(relaxed = true)

    private lateinit var repository: PropertyRepositoryImpl

    @Before
    fun setUp() {
        repository = PropertyRepositoryImpl(api, dao, mapper, auth, firestore, aiService, context)
    }

    @Test
    fun `getFavorites reads from DAO ensuring offline-first pattern`() = runTest {
        // Arrange
        val mockList = listOf(
            PropertyEntity(
                id = "1", title = "Favorite 1", price = 100.0, currency = "USD",
                operationType = "B", surface = 1.0, rooms = 1,
                latitude = 0.0, longitude = 0.0, isFavorite = true, lastViewedAt = 0L,
                description = "Desc", images = listOf(),
                zone = "Zone", bathrooms = 1, imageUrl = "", contactPhone = "", lastUpdated = 0L
            )
        )
        every { dao.getFavorites() } returns flowOf(mockList)

        // Act
        val result = repository.getFavorites().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Favorite 1", result[0].title)
        assertEquals(true, result[0].isFavorite)
    }
    
    @Test
    fun `getPropertyById reads directly from local Room Cache`() = runTest {
        // Arrange
        val mockProperty = PropertyEntity(
            id = "1", title = "Test", price = 100.0, currency = "USD",
            operationType = "B", surface = 1.0, rooms = 1,
            latitude = 0.0, longitude = 0.0, isFavorite = false, lastViewedAt = 0L,
            description = "Desc", images = listOf(),
            zone = "Zone", bathrooms = 1, imageUrl = "", contactPhone = "", lastUpdated = 0L
        )
        every { dao.getPropertyById("1") } returns flowOf(mockProperty)

        // Act
        val result = repository.getPropertyById("1").first()

        // Assert
        assertEquals("Test", result?.title)
    }
}
