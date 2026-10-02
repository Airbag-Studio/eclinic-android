package it.airbagstudio.ticare

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.airbagstudio.ticare.crash.ErrorReporting

@HiltAndroidApp
class EclinicApplication: Application() {

    override fun onCreate() {
        // Per primo: un crash nell'avvio di Hilt o delle schermate arriva a Sentry.
        ErrorReporting.init(this)
        super.onCreate()
    }
}
