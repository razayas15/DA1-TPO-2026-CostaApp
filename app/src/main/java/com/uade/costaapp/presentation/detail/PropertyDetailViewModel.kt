package com.uade.costaapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _property = MutableStateFlow<PropertyEntity?>(null)
    val property: StateFlow<PropertyEntity?> = _property.asStateFlow()

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
}
