package it.airbagstudio.ticare.pages.patientsList.downloadPatientData

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.CaseInfo
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientsList.PatientListUiState
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPatientsDialogScreen(
    viewModel: SelectPatientsDataViewModel = hiltViewModel(),
    cases: List<PatientListUiState.PatientUIState>,
    onDismissRequest: () -> Unit
) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.setCases(cases)
    })

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = uiState.syncState.isCompleted, block = {
        if (uiState.syncState.isCompleted) {
            viewModel.resetData()
            onDismissRequest()
        }
    })

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            viewModel.startDownloadPatientData()
        }
    }

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.download_patient_data))
                        }

                    },
                    actions = {
                        if (!uiState.syncState.isDownloading) {
                            IconButton(onClick = { onDismissRequest() }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "")
                            }
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values)
            ) {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(id = R.string.download_patient_data_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (uiState.syncState.isDownloading) {
                    Spacer(modifier = Modifier.weight(0.8f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.downloading_selected_patient_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            progress = uiState.syncState.progress
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.2f))
                } else {

                    TextField(
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.search))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        value = uiState.query,
                        onValueChange = {
                            viewModel.setQuery(it)
                        }
                    )
                    Box {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 100.dp),
                            content = {
                                items(uiState.patients) {
                                    ListItem(
                                        modifier = Modifier.clickable {
                                            viewModel.togglePatientSelection(it.patientCode)
                                        },
                                        headlineContent = {
                                            Text(text = it.name)
                                        },
                                        leadingContent = {
                                            val painter =
                                                if (it.isSelected) painterResource(id = R.drawable.ic_patient_selected) else painterResource(
                                                    id = R.drawable.ic_patient_unselected
                                                )
                                            Image(painter = painter, contentDescription = "")
                                        }
                                    )
                                    Divider()
                                }
                            })
                        if (uiState.patients.count { it.isSelected } > 0) {
                            Column(
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Column(Modifier.background(Color.White)) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = seed
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        onClick = {
                                            /*
                                            val permissionCheckResult =
                                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                                viewModel.startDownloadPatientData()
                                            } else {
                                                // Request a permission
                                                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            }

                                             */
                                            viewModel.startDownloadPatientData()
                                        }) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = ""
                                        )
                                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                                        Text(text = stringResource(id = R.string.download_patient_data))
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = {
                    viewModel.errorMessage = null
                    onDismissRequest()
                })
        }

    }
}