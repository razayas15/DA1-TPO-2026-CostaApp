package com.uade.costaapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedOperation = MutableStateFlow("Todas")
    val selectedOperation = _selectedOperation.asStateFlow()

    private val _selectedSort = MutableStateFlow("Relevancia")
    val selectedSort = _selectedSort.asStateFlow()

    private val allProperties = repository.getAllProperties()

    val recommendedProperties: StateFlow<List<PropertyEntity>> = allProperties
        .map { it.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val properties: StateFlow<List<PropertyEntity>> = combine(
        allProperties,
        _searchQuery,
        _selectedOperation,
        _selectedSort
    ) { propertiesList, query, operation, sort ->
        var filtered = propertiesList

        if (operation == "Venta") {
            filtered = filtered.filter { it.operationType.equals("sale", true) || it.operationType.equals("venta", true) }
        } else if (operation == "Alquiler") {
            filtered = filtered.filter { it.operationType.equals("rent", true) || it.operationType.equals("alquiler", true) }
        }

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) || it.zone.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            "Menor precio" -> filtered = filtered.sortedBy { it.price }
            "Mayor precio" -> filtered = filtered.sortedByDescending { it.price }
            "Más ambientes" -> filtered = filtered.sortedByDescending { it.rooms }
            "Mayor superficie" -> filtered = filtered.sortedByDescending { it.surface }
        }

        filtered
    }.stateIn(
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
                repository.refreshProperties()
            } catch (e: Exception) {
                // Modo offline
            }
        }
    }

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onOperationChanged(operation: String) { _selectedOperation.value = operation }
    fun onSortChanged(sort: String) { _selectedSort.value = sort }
}
