package com.uade.costaapp.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(context: Context, webClientId: String): FirebaseUser?
    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Boolean
    fun signOut()
}
