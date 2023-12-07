package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerButton
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed

enum class SyncButtonState {
    ONLINE, SYNCING, OFFLINE
}

@Composable
private fun SyncButton(state: SyncButtonState) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        TextButton(onClick = {
            expanded = !expanded
        }) {
            val message = when (state) {
                SyncButtonState.ONLINE -> stringResource(id = R.string.online)
                SyncButtonState.SYNCING -> stringResource(id = R.string.syncing)
                SyncButtonState.OFFLINE -> stringResource(id = R.string.offline)
            }
            val icon = when (state) {
                SyncButtonState.ONLINE -> painterResource(id = R.drawable.ic_online)
                SyncButtonState.SYNCING -> painterResource(id = R.drawable.ic_syncing)
                SyncButtonState.OFFLINE -> painterResource(id = R.drawable.ic_offline)
            }
            /*
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = if (state == SyncButtonState.OFFLINE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

             */
            Image(painter = icon, contentDescription = message)
        }
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                onDismissRequest = { expanded = false },
            ) {
                val dropdownIcon = when (state) {
                    SyncButtonState.ONLINE -> painterResource(id = R.drawable.ic_check)
                    SyncButtonState.SYNCING -> painterResource(id = R.drawable.ic_check)
                    SyncButtonState.OFFLINE -> painterResource(id = R.drawable.ic_error)
                }

                DropdownMenuItem(
                    text = {
                        Row() {
                            Image(
                                painter = dropdownIcon,
                                contentDescription = state.name,
                                alignment = Alignment.TopCenter,
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.size(width = 40.dp, height = 40.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.padding(bottom = 8.dp)

                            ) {
                                Text(
                                    text = stringResource(id = R.string.last_sync),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(id = R.string.just_now),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                /*
                                Text(
                                    text = stringResource(id = R.string.sync_problem),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                 */
                            }
                        }


                    }, onClick = { /*TODO*/ }
                )
                Divider()
                DropdownMenuItem(
                    text = {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = seed
                            ),
                            onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sync),
                                contentDescription = stringResource(id = R.string.sync_now),
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = stringResource(id = R.string.sync_now))
                        }

                    }, onClick = { /*TODO*/ }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithSyncAndSettings(title: String, onSettingsClick: () -> Unit) {
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
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = { onSettingsClick() }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_hamburger),
                    contentDescription = "settings"
                )
            }
        },
        actions = {
            SyncButton(state = SyncButtonState.ONLINE)
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
            SyncButton(state = SyncButtonState.ONLINE)
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
        ToolbarWithSyncAndSettings(title = "Casa Delle Rose") {

        }
    }

}