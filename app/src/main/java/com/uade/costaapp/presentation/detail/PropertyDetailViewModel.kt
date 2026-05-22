package com.uade.costaapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val propertyId: String = checkNotNull(savedStateHandle["propertyId"])

    val property: StateFlow<PropertyEntity?> = repository.getPropertyById(propertyId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
