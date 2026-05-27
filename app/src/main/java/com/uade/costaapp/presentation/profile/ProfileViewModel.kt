package com.uade.costaapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val repository: PropertyRepository
) : ViewModel() {

    private val _userName = MutableStateFlow("Usuario Invitado")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    val favoritesCount: StateFlow<Int> = repository.getFavoritesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val viewedCount: StateFlow<Int> = repository.getViewedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        firebaseAuth.currentUser?.let { user ->
            _userName.value = user.displayName ?: "Usuario"
            _userEmail.value = user.email ?: ""
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
