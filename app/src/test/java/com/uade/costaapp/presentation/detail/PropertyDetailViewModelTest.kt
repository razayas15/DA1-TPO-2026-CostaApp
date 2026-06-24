package com.uade.costaapp.presentation.detail

import app.cash.turbine.test
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.data.remote.AiAnalysisData
import com.uade.costaapp.domain.repository.PropertyRepository
import com.uade.costaapp.utils.NoInternetException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PropertyDetailViewModelTest {

    private val repository: PropertyRepository = mockk(relaxed = true)
    private lateinit var viewModel: PropertyDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockProperty = PropertyEntity(
        id = "1",
        title = "Test Property",
        price = 100000.0,
        currency = "USD",
        operationType = "Sale",
        surface = 100.0,
        rooms = 3,
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = false,
        lastViewedAt = 0L,
        description = "Test description",
        images = listOf(),
        zone = "Norte",
        bathrooms = 2,
        imageUrl = "",
        contactPhone = "123",
        lastUpdated = 0L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PropertyDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProperty emits property state correctly`() = runTest(testDispatcher) {
        // Arrange
        every { repository.getPropertyById("1") } returns flowOf(mockProperty)

        // Act
        viewModel.loadProperty("1")
        advanceUntilIdle()

        // Assert
        assertEquals(mockProperty, viewModel.property.value)
    }

    @Test
    fun `analyzeProperty emits Success state when network and API succeed`() = runTest(testDispatcher) {
        // Arrange
        val mockAnalysis = AiAnalysisData(summary = "Success", positives = listOf("A", "B", "C"), warnings = listOf())
        every { repository.getPropertyById("1") } returns flowOf(mockProperty)
        coEvery { repository.generarAnalisisIA(any()) } returns mockAnalysis
        
        viewModel.loadProperty("1")
        advanceUntilIdle()

        // Act & Assert
        viewModel.aiState.test {
            assertEquals(AiState.Idle, awaitItem())
            
            viewModel.analyzeProperty()
            
            assertEquals(AiState.Loading, awaitItem())
            assertEquals(AiState.Thinking, awaitItem())
            
            val successState = awaitItem()
            assertTrue(successState is AiState.Success)
            assertEquals("Success", (successState as AiState.Success).data.summary)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `analyzeProperty emits Error state when no internet`() = runTest(testDispatcher) {
        // Arrange
        every { repository.getPropertyById("1") } returns flowOf(mockProperty)
        coEvery { repository.generarAnalisisIA(any()) } throws NoInternetException()
        
        viewModel.loadProperty("1")
        advanceUntilIdle()

        // Act & Assert
        viewModel.aiState.test {
            assertEquals(AiState.Idle, awaitItem())
            
            viewModel.analyzeProperty()
            
            assertEquals(AiState.Loading, awaitItem())
            assertEquals(AiState.Thinking, awaitItem())
            
            val errorState = awaitItem()
            assertTrue(errorState is AiState.Error)
            assertEquals("No hay conexión a internet", (errorState as AiState.Error).message)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
