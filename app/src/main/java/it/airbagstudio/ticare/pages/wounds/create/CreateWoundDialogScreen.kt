package it.airbagstudio.ticare.pages.wounds.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.wounds.common.AddingImagesGallery
import it.airbagstudio.ticare.ui.components.AddPhotoButton
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.MultiselectPopupTextField
import it.airbagstudio.ticare.ui.components.PopupTextField
import it.airbagstudio.ticare.ui.components.getUnsafeOkHttpClient
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.getPainter
import it.airbagstudio.ticare.utils.rememberImeState
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWoundDialogScreen(
    codCase: String,
    woundId: Int?= null,
    canWrite: Boolean,
    onDismissRequest: (Boolean) -> Unit,
    viewModel: CreateWoundDialogScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.clearData()
        viewModel.codCase = codCase
        viewModel.downloadData(woundId)
    }
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()
    LaunchedEffect(key1 = imeState.value, block = {
        if (imeState.value){
            scrollState.scrollTo(scrollState.maxValue)
        }
    })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.wound_protocol))
                        }
                    },
                    actions = {
                        IconButton(onClick = { onDismissRequest(false) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canWrite,
                    date = uiState.wound.date, label = {
                        Text(text = stringResource(id = R.string.appearance_date))
                    }, onDateChanged = {
                        viewModel.setDate(it)
                    })
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.injury_type),
                    value = uiState.wound.woundType ?: "",
                    items = viewModel.woundTypes.value,
                    enabled = canWrite,
                ){
                    viewModel.setWoundType(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                MultiselectPopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.position),
                    items = viewModel.woundBodyParts.value,
                    selectedItems = uiState.dropdownSelections.selectedWoundBodyParts?.map { ListPopupItem(it.name,it) } ?: listOf(),
                    enabled = canWrite,
                ){ selectedItems  ->
                    selectedItems?.mapNotNull { it.item }?.let { viewModel.setWoundBodyParts(it) } ?: run { viewModel.setWoundBodyParts(null) }
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.origin),
                    value = uiState.wound.origin ?: "",
                    items = viewModel.woundOrigins.value,
                    enabled = canWrite,
                ){
                    viewModel.setWoundOrigin(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.length?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.length ?: "",
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                viewModel.setLength(it)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.length))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        },
                        enabled = canWrite,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.width?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.width ?: "",
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                viewModel.setWidth(it)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.width))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        },
                        enabled = canWrite,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.depth?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.depth ?: "",
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                viewModel.setDepth(it)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.depth))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        },
                        enabled = canWrite,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    placeholder = {
                                  Text(text = stringResource(id = R.string.description))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    value = uiState.wound.description ?: "",
                    onValueChange = {
                        viewModel.setNotes(it)
                    },
                    enabled = canWrite,
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.wound.images.isNotEmpty() || uiState.woundPhotos.isNotEmpty()) {
                    val imageLoader = ImageLoader.Builder(LocalContext.current)
                        .okHttpClient(getUnsafeOkHttpClient())
                        .build()
                    val painters = uiState.woundPhotos.map {
                        it.getPainter(requestData = viewModel.requestImageRequestData, imageLoader = imageLoader, authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(
                            Instant.now()), isOnline = uiState.isOnline)
                    }
                    AddingImagesGallery(painters = painters, images = uiState.wound.images) {
                        viewModel.removeImage(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AddPhotoButton(onSuccess = {
                    viewModel.addImages(it)
                })
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    enabled = uiState.isValid && canWrite && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.saveWound()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.execute)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.save))
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (imeState.value){
                    Spacer(modifier = Modifier.height(150.dp))
                }
            }
        }
        if (viewModel.errorMessage != null){
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                viewModel.errorMessage = null
            })
        }
        if (uiState.isSuccess){
            viewModel.clearData()
            onDismissRequest(true)
        }
    }
}

@Composable
@Preview
private fun CreateWoundDialogScreenPreview() {
    AppTheme {
        CreateWoundDialogScreen("", canWrite = true, onDismissRequest = {})
    }
}