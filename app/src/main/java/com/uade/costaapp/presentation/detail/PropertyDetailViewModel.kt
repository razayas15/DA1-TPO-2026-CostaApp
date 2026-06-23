package com.uade.costaapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.data.remote.AiAnalysisData
import com.uade.costaapp.data.remote.AiService
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    object Thinking : AiState()
    data class Success(val data: AiAnalysisData) : AiState()
    data class Error(val message: String) : AiState()
}

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val aiService: AiService
) : ViewModel() {

    private val _property = MutableStateFlow<PropertyEntity?>(null)
    val property: StateFlow<PropertyEntity?> = _property.asStateFlow()

    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            repository.getPropertyById(id).collectLatest { entity ->
                _property.value = entity
            }
        }
    }

    fun toggleFavorite() {
        val currentProperty = _property.value ?: return
        viewModelScope.launch {
            try {
                repository.updateFavorite(currentProperty.id, !currentProperty.isFavorite)
            } catch (e: Exception) {
                // Manejar error de base de datos
            }
        }
    }

    fun analyzeProperty() {
        val currentProperty = _property.value ?: return
        if (_aiState.value !is AiState.Idle) return

        viewModelScope.launch(Dispatchers.IO) {
            _aiState.value = AiState.Loading
            delay(500)
            _aiState.value = AiState.Thinking
            delay(1500) // Sensación de pensando
            try {
                val prompt = "Genera 3 puntos fuertes atractivos de esta propiedad basados en sus datos, en formato de viñetas cortas. Propiedad: ${currentProperty.title}, ${currentProperty.rooms} amb, ${currentProperty.surface} m2, Precio: ${currentProperty.price} USD. No incluyas desventajas."
                val result = aiService.getAnalysis(prompt)
                _aiState.value = AiState.Success(result)
            } catch (e: Exception) {
                _aiState.value = AiState.Error("Error al analizar la propiedad.")
            }
        }
    }
}
