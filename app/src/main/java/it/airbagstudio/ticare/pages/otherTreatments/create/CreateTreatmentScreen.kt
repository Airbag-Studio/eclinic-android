package it.airbagstudio.ticare.pages.otherTreatments.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.OtherService
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getExecDateTime
import it.airbagstudio.ticare.utils.toDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTreatmentScreen(
    viewModel: CreateTreatmentScreenViewModel = hiltViewModel(),
    articleId: Int,
    patientCode: String,
    service: OtherService? = null,
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        LaunchedEffect(Unit) {
            viewModel.patientCode.value = patientCode
            service?.let {_service ->
               viewModel.setService(_service)
            } ?: run{
                viewModel.selectedArticleId.value = articleId
                viewModel.setGuarantorId(null)
                viewModel.setNotes("")
                viewModel.setQuantity(1.0)
                viewModel.setDate(Date())
            }
        }
        BuildSheetContent(viewModel = viewModel,onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(viewModel: CreateTreatmentScreenViewModel, onDismissRequest: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedDate by remember {
        mutableStateOf(uiState.newTreatment.date)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showGuarantorPopup by remember {
        mutableStateOf(false)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDate.hours,
        initialMinute = selectedDate.minutes
    )
    val column1Weight = 0.6f
    val column2Weight = 1 - column1Weight
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
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
            OutlinedTextField(
                modifier = Modifier
                    .clickable {
                        showDatePicker = true
                    }
                    .weight(column1Weight),
                value = uiState.newTreatment.date.format("dd MMMM yyyy, HH:mm "),
                enabled = false,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                onValueChange = {},
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_field_calendar),
                        stringResource(id = R.string.actual_date_time)
                    )
                },
                label = { Text(text = stringResource(id = R.string.actual_date_time)) }
            )
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
        Box(modifier = Modifier.height(500.dp)) {
            if (showDatePicker) {
                Column() {
                    DatePicker(
                        dateValidator = {
                            val date = Date(it)
                            date.before(Date())
                        },
                        state = datePickerState,
                        headline = null,
                        title = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            currentYearContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Row() {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = { showDatePicker = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                showTimePicker = true
                                showDatePicker = false
                            }) {
                            Text(
                                text = stringResource(id = R.string.ok)
                            )
                        }
                    }
                }

            } else if (showTimePicker) {
                Column(
                    modifier = Modifier.padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectorColor = MaterialTheme.colorScheme.primary,
                            timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                    Row() {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                showTimePicker = false
                                showDatePicker = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                selectedDate =
                                    if (datePickerState.selectedDateMillis != null) Date(
                                        datePickerState.selectedDateMillis!!
                                    ) else Date()
                                selectedDate.hours = timePickerState.hour
                                selectedDate.minutes = timePickerState.minute
                                viewModel.setDate(selectedDate)
                                showTimePicker = false
                                showDatePicker = false

                            }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                }

            } else {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(column1Weight)) {
                            OutlinedTextField(
                                maxLines = 1,
                                value = uiState.newTreatment.guarantorType?.name ?: stringResource(
                                    id = R.string.no_guarantor
                                ),
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
                                })

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
                            value = if (uiState.newTreatment.quantity != null) String.format(
                                "%.1f",
                                uiState.newTreatment.quantity
                            ) else "",
                            label = {
                                Text(text = stringResource(id = R.string.quantity))
                            },
                            onValueChange = {
                                val quantity = it.toDoubleOrNull()
                                if (it.isEmpty()) {
                                    viewModel.setQuantity(null)
                                } else {
                                    viewModel.setQuantity(quantity)
                                }

                            })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.notes))
                        },
                        value = uiState.newTreatment.description, onValueChange = {
                            viewModel.setNotes(it)
                        })
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
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

            }
        }
        if (uiState.error != null) {
            ErrorAlert(
                message = uiState.error!!,
                onDismissRequest = {
                    viewModel.clearState()
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
                } + ListPopupItem(
                    stringResource(id = R.string.no_guarantor), item = null
                ),
                setShowDialog = {
                    showGuarantorPopup = false
                },
                onItemSelected = {
                    viewModel.setGuarantorId(it.item?.id)
                    showGuarantorPopup = false
                })
        }
        if (uiState.isSuccess){
            viewModel.clearState()
            onDismissRequest()
        }
    }
}