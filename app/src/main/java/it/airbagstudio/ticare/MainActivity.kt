package it.airbagstudio.ticare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import ch.ticare.eclinic.library.network.CredentialsListener
import dagger.hilt.android.AndroidEntryPoint
import it.airbagstudio.ticare.navigation.EclinicNavGraph
import it.airbagstudio.ticare.ui.theme.AppTheme
object LoginRedirect: CredentialsListener{
    var onCredentialRefresh: (() -> Unit)? = null

    override fun needCredentialsRefresh() {
        onCredentialRefresh?.invoke()
    }

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                EclinicNavGraph()
            }
        }
    }
}