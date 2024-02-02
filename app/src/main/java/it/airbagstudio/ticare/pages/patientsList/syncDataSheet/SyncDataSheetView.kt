package it.airbagstudio.ticare.pages.patientsList.syncDataSheet

import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
fun SyncDataSheetView(
    viewModel: SyncDataSheetViewModel = hiltViewModel(),
    onDismissRequest: (Boolean) -> Unit
){
    val captureController = rememberCaptureController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val contentResolver = LocalContext.current.contentResolver
    Dialog(
        onDismissRequest = {
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
                }else if (uiState.isCompleted == uiState.errors > 0){
                    ErrorMessageContainer(){
// Capture content
                        val bitmapAsync = captureController.captureAsync()
                        GlobalScope.async {
                            try {
                                val bitmap = bitmapAsync.await().asAndroidBitmap()
                                MediaStore.Images.Media.insertImage(contentResolver, bitmap,"erroy_sync_${Date().format("dd_MM_yyyy_HH_mm")}", "");
                            } catch (error: Throwable) {
                                error.printStackTrace()
                                // Error occurred, do something.
                            }
                        }

                    }
                    LazyColumn(
                        modifier = Modifier
                            .background(Color.White)
                            .capturable(captureController),
                        content = {
                            items(10){
                                Row(Modifier.background(Color.White)) {
                                    Text(

                                        text = "Item $it"
                                    )
                                }

                            }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = seed
                        ),
                        onClick = {
                            onDismissRequest(true)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }else if (uiState.isCompleted){
                    viewModel.resetData()
                    onDismissRequest(true)
                }
            }
        }
    }
}

@Composable
fun ErrorMessageContainer(onTakeScreenShot: () -> Unit){
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
            border = BorderStroke(1.dp,MaterialTheme.colorScheme.onErrorContainer),
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
private fun PreviewErrorMessageContainer(){
    AppTheme {
        SyncDataSheetView(viewModel = SyncDataSheetViewModel(null)){}
    }
}