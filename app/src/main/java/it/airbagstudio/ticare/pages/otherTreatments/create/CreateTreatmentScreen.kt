package it.airbagstudio.ticare.pages.otherTreatments.create

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.OtherService
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTreatmentScreen(
    viewModel: CreateTreatmentScreenViewModel = hiltViewModel(),
    articleId: Int,
    patientCode: String,
    service: OtherService? = null,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        LaunchedEffect(Unit) {
            viewModel.patientCode.value = patientCode
            service?.let { _service ->
                viewModel.setService(_service)
            } ?: run {
                viewModel.setService(null)
                viewModel.selectedArticleId.value = articleId
                viewModel.setGuarantorId(null)
                viewModel.setNotes("")
                viewModel.setQuantity("1.0")
                viewModel.setDate(Date())
            }
        }
        BuildSheetContent(viewModel = viewModel, onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(
    viewModel: CreateTreatmentScreenViewModel,
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showGuarantorPopup by remember {
        mutableStateOf(false)
    }
    val column1Weight = 0.6f
    val column2Weight = 1 - column1Weight
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.new_nursing_course))
                    }

                },
                actions = {
                    IconButton(onClick = { onDismissRequest() }) {
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
            Text(
                text = uiState.newTreatment.article?.desc ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row() {
                Text(
                    text = uiState.newTreatment.article?.group ?: "",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = uiState.newTreatment.article?.code ?: "",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarTextField(
                    modifier = Modifier.weight(column1Weight),
                    date = uiState.newTreatment.date,
                    label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                    onDateChanged = {
                        viewModel.setDate(it)
                    })
                /*
            Spacer(modifier = Modifier.width(24.dp))
            OutlinedTextField(
                maxLines = 1,
                label = {
                        Text(text = stringResource(id = R.string.duration))
                },
                modifier = Modifier.weight(column2Weight),
                value = uiState.duration.toString(), onValueChange = {
                duration.value = it.toIntOrNull() ?: 0
            })

             */
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(column1Weight)) {
                    OutlinedTextField(
                        maxLines = 1,
                        value = "          ",
                        label = {
                            Text(text = stringResource(id = R.string.guarantor))
                        },
                        onValueChange = {

                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.id_dropdown),
                                contentDescription = ""
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .matchParentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 24.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            text = uiState.newTreatment.guarantorType?.name
                                ?: stringResource(
                                    id = R.string.no_guarantor
                                )
                        )
                    }
                    Box(modifier = Modifier
                        .matchParentSize()
                        .alpha(0f)
                        .clickable {
                            showGuarantorPopup = true
                        })
                }
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    maxLines = 1,
                    modifier = Modifier.weight(column2Weight),
                    value = uiState.newTreatment.quantity ?: "0",
                    label = {
                        Text(text = stringResource(id = R.string.quantity))
                    },
                    onValueChange = {
                        viewModel.setQuantity(it)
                    },
                    isError = uiState.newTreatment.quantity?.toDoubleOrNull() == null

                )


            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.notes))
                },
                value = uiState.newTreatment.description, onValueChange = {
                    viewModel.setNotes(it)
                })
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                enabled = uiState.newTreatment.quantity?.toDoubleOrNull() != null && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.saveTreatment()
                }) {
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
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = {
                    viewModel.errorMessage = null
                })
        }
        if (showGuarantorPopup) {
            ListPopup(
                title = stringResource(id = R.string.guarantor),
                items = uiState.guarantorTypes.map {
                    ListPopupItem(
                        label = it.name,
                        item = it
                    )
                },
                setShowDialog = {
                    showGuarantorPopup = false
                },
                onItemSelected = {
                    viewModel.setGuarantorId(it.item?.id)
                    showGuarantorPopup = false
                })
        }
        if (uiState.isSuccess) {
            viewModel.clearState()
            onDismissRequest()
        }
    }
}
