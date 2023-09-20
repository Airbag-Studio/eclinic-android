package it.airbagstudio.ticare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import it.airbagstudio.ticare.navigation.EclinicNavGraph
import it.airbagstudio.ticare.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                EclinicNavGraph()
            }
        }
    }
}