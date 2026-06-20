package com.uade.costaapp.presentation.home

import com.uade.costaapp.domain.repository.PropertyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: PropertyRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        // 1. Arrange: Configuramos el Mock del Repository usando MockK
        repository = mockk<PropertyRepository>()

        // Simulamos un flujo de Room vacío para que la inicialización del ViewModel no crashee
        coEvery { repository.getAllProperties() } returns flowOf(emptyList())
        
        // 2. Act: Instanciamos el ViewModel (inyectando el Mock)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `cuando cambia la busqueda, el StateFlow se actualiza correctamente`() = runTest {
        // Act
        viewModel.onSearchQueryChanged("Bunge")
        
        // Assert
        assertEquals("Bunge", viewModel.searchQuery.value)
    }

    @Test
    fun `cuando cambia el filtro de operacion, el StateFlow refleja el cambio`() = runTest {
        // Act
        viewModel.onOperationChanged("Venta")
        
        // Assert
        assertEquals("Venta", viewModel.selectedOperation.value)
    }
}
