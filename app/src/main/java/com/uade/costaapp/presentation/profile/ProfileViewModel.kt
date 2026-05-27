package com.uade.costaapp.presentation.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _userName = MutableStateFlow("Usuario Invitado")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

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
