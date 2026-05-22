package com.uade.costaapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    // Recolectamos el Flow reactivo desde la Base de Datos (Offline-First)
    val properties: StateFlow<List<PropertyEntity>> = repository.getAllProperties()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        fetchProperties()
    }

    private fun fetchProperties() {
        viewModelScope.launch {
            try {
                // Forzamos la actualización desde la API. Si falla, el Flow de arriba 
                // seguirá mostrando lo que esté guardado en Room.
                repository.refreshProperties()
            } catch (e: Exception) {
                // Manejo de error de red (la UI sigue mostrando Room de manera transparente)
            }
        }
    }
}
