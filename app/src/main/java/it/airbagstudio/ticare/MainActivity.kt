package it.airbagstudio.ticare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import ch.ticare.eclinic.library.network.CredentialsListener
import dagger.hilt.android.AndroidEntryPoint
import it.airbagstudio.ticare.navigation.EclinicNavGraph
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.theme.AppTheme

object LoginRedirect: CredentialsListener{
    var onCredentialRefresh: (() -> Unit)? = null

    override fun needCredentialsRefresh() {
        onCredentialRefresh?.invoke()
    }

}
val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not present")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val myViewModel: TimeTrackerViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                CompositionLocalProvider(LocalActivity provides this@MainActivity) {
                    EclinicNavGraph()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.checkIfDataIsExpired()
    }
}