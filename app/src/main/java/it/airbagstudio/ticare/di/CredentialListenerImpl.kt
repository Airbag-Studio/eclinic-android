package it.airbagstudio.ticare.di

import android.app.Activity
import android.content.Context
import android.content.Intent
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.network.CredentialsListener
import it.airbagstudio.ticare.MainActivity


class CredentialListenerImpl(private val context: Context,private val authRepository: AuthRepository): CredentialsListener {

    override fun needCredentialsRefresh() {
        authRepository.setRefreshToken(null)
        authRepository.setToken(null)
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            (context as Activity).finish()
        }
        Runtime.getRuntime().exit(0)
    }
}