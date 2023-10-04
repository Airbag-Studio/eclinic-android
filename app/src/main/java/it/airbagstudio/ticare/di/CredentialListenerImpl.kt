package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.network.CredentialsListener


class CredentialListenerImpl(private val context: Context,private val authRepository: AuthRepository): CredentialsListener {

     var onCredentialRefresh: (() -> Unit)? = null

    override fun needCredentialsRefresh() {
        authRepository.setRefreshToken(null)
        authRepository.setToken(null)
        onCredentialRefresh?.invoke()
    }
}