package it.airbagstudio.ticare.pages.patientsList.syncDataSheet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.repository.SyncDataRepository
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientsList.PatientListUiState
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.LocalNotificationReceiver
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
fun SyncDataSheetView(
    viewModel: SyncDataSheetViewModel = hiltViewModel(),
    caseList: List<PatientListUiState.PatientUIState>,
    onDismissRequest: (Boolean) -> Unit
) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.resetData()
        viewModel.caseList = caseList
        viewModel.startUpload()
    })
    val captureController = rememberCaptureController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()
    Dialog(
        onDismissRequest = {
            viewModel.resetData()
            onDismissRequest(false)
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.sync_data))
                        }

                    }
                )
            }) { values ->
            if (viewModel.syncNetworkError != null) {
                AlertDialog(
                    title = {
                        Text(text = stringResource(id = R.string.warning))
                    },
                    text = {
                        Text(text = viewModel.syncNetworkError!!)
                    },
                    onDismissRequest = {
                        viewModel.resetData()
                        onDismissRequest(false)
                    }, confirmButton = {
                        Button(onClick = {
                            viewModel.resetData()
                            onDismissRequest(false)
                        }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values)
            ) {
                if (uiState.isSyncInProgress) {
                    Spacer(modifier = Modifier.weight(0.8f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.upload_patient_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            progress = uiState.progress
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.2f))
                } else if (uiState.isCompleted && uiState.syncDataError.isNotEmpty()) {
                    ErrorMessageContainer() {
// Capture content
                        val bitmapAsync = captureController.captureAsync()
                        scope.launch {
                            try {
                                val bitmap = bitmapAsync.await().asAndroidBitmap()
                                MediaStore.Images.Media.insertImage(
                                    contentResolver,
                                    bitmap,
                                    "erroy_sync_${Date().format("dd_MM_yyyy_HH_mm")}",
                                    ""
                                );
                            } catch (error: Throwable) {
                                error.printStackTrace()
                                // Error occurred, do something.
                            }
                        }

                    }
                    ListErrorContainer(captureController, uiState.syncDataError)
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = seed
                        ),
                        onClick = {
                            viewModel.resetData()
                            removePendingNotifications(context)
                            onDismissRequest(true)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                } else if (uiState.isCompleted) {
                    viewModel.resetData()
                    onDismissRequest(true)
                    removePendingNotifications(context)
                }
            }
        }
    }
}

private fun removePendingNotifications(context: Context) {
    val alarmManager =
        ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager
    val alarmIntent = Intent(context, LocalNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListErrorContainer(
    captureController: CaptureController,
    errors: List<SyncDataSheetViewModelUIState.SyncDataError>
) {
    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .capturable(captureController),
        content = {
            items(errors) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_account_circle),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it.completeName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = it.birthDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    it.errors.forEach { patientError ->
                        ListItem(
                            colors = ListItemDefaults.colors(
                                containerColor = Color.White
                            ),
                            overlineContent = {
                                Text(text = stringResource(id = patientError.typeStringId))
                            },
                            trailingContent = {
                                Column {
                                    Text(text = patientError.time)
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                            },
                            headlineContent = {
                                Text(text = patientError.title)
                            }
                        )
                        if (patientError != it.errors.last()) {
                            HorizontalDivider()
                        }
                    }
                }

            }
        }
    )
}

@Composable
fun ErrorMessageContainer(onTakeScreenShot: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp)
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.sync_errors_message),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(1.dp))
        OutlinedButton(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            onClick = {
                onTakeScreenShot()

            }
        ) {
            Text(text = stringResource(id = R.string.take_screenshot))
        }

    }
}

@Composable
@Preview
private fun PreviewListErrors() {
    val captureController = rememberCaptureController()
    AppTheme {
        ListErrorContainer(
            captureController = captureController, errors = listOf(
                SyncDataSheetViewModelUIState.SyncDataError(
                    "28.12.1926 (97)",
                    "28.12.1926 (97)",
                    "Antonietti Raffaella",
                    listOf(
                        SyncDataSheetViewModelUIState.SyncDataError.PatientError(
                            R.string.wounds,
                            "Meto Zeroch cpr ret 25mg",
                            "09:45"
                        )
                    )
                )
            )
        )
    }
}