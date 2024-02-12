package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerButton
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithSyncAndSettings(title: String, onSettingsClick: () -> Unit,isOnline: Boolean,onDownloadPatientDataClick:()->Unit) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            FilledIconButton(
                enabled = isOnline,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                onClick = { onSettingsClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hamburger),
                    contentDescription = "settings"
                )
            }
        },
        actions = {
            FilledIconButton(
                enabled = isOnline,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                onClick = {
                onDownloadPatientDataClick()
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_downalod_patient_data), contentDescription = "Downalod")
            }
            TimeTrackerButton()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithBackAndSync(title: String, onBack: () -> Unit) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
        },
        actions = {
            TimeTrackerButton()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithBack(title: String,actions: @Composable() (RowScope.() -> Unit) = {}, onBack: () -> Unit, ) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
        },
        actions = actions


    )
}

@Composable
@Preview
private fun PreviewToolbar() {
    AppTheme() {
        ToolbarWithSyncAndSettings(title = "Casa Delle Rose", onDownloadPatientDataClick = {},
            isOnline = true,onSettingsClick = {})
    }

}