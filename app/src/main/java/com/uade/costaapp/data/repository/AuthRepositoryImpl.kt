package com.uade.costaapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.uade.costaapp.data.remote.auth.AuthDataSource
import com.uade.costaapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun signInWithGoogle(context: Context, webClientId: String): FirebaseUser? {
        return authDataSource.signInWithGoogle(context, webClientId)
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
    }

    override fun isUserLoggedIn(): Boolean {
        return authDataSource.getCurrentUser() != null
    }

    override fun signOut() {
        authDataSource.signOut()
    }
}
