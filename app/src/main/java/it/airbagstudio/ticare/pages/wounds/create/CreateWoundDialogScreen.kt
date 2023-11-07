package it.airbagstudio.ticare.pages.wounds.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.wounds.AddingImagesGallery
import it.airbagstudio.ticare.ui.components.AddPhotoButton
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.PlaceholderTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWoundDialogScreen(
    onDismissRequest: (Boolean) -> Unit,
    viewModel: CreateWoundDialogScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {

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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    date = uiState.wound.date, label = {
                        Text(text = stringResource(id = R.string.appearance_date))
                    }, onDateChanged = {
                        viewModel.setDate(it)
                    })
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.length?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.length ?: "",
                        onValueChange = {
                            viewModel.setLength(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.length))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.width?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.width ?: "",
                        onValueChange = {
                            viewModel.setWidth(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.width))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        visualTransformation = if (uiState.wound.depth?.isNullOrEmpty() == true) PlaceholderTransformation("--") else VisualTransformation.None,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = uiState.wound.depth ?: "",
                        onValueChange = {
                            viewModel.setDepth(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.depth))
                        },
                        supportingText = {
                            Text(text = stringResource(id = R.string.mm))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    placeholder = {
                                  Text(text = stringResource(id = R.string.description))
                    },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    value = uiState.wound.description ?: "",
                    onValueChange = {
                    viewModel.setNotes(it)
                })

                Spacer(modifier = Modifier.height(16.dp))
                Divider(Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.wound.images.isNotEmpty()) {
                    AddingImagesGallery(uiState.wound.images) {
                        viewModel.removeImage(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AddPhotoButton(onSuccess = {
                    viewModel.addImages(it)
                })

            }
        }
    }
}

@Composable
@Preview
private fun CreateWoundDialogScreenPreview() {
    AppTheme {
        CreateWoundDialogScreen(onDismissRequest = {})
    }
}