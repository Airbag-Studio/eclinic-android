package it.airbagstudio.ticare.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import it.airbagstudio.ticare.BuildConfig

@Composable
fun AppVersionText(modifier: Modifier = Modifier){
    val appVersion = BuildConfig.VERSION_NAME
    val buildNumber = BuildConfig.VERSION_CODE
    Text(
        modifier = modifier,
        text = "Ver. $appVersion ($buildNumber)",
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}