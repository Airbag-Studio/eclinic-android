package it.airbagstudio.ticare.pages.nursingCourses.details

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareCourse
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.utils.format
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNursingCourseScreen(
    viewModel: EditNursingCourseSheetViewModel = hiltViewModel(),
    patientCode: String,
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        LaunchedEffect(Unit) {
            run {
                viewModel.setScreenType(ScreenType.Add)
                viewModel.loadCategory()
            }
        }
        BuildSheetContent(viewModel = viewModel, patientCode, onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNursingCourseScreen(
    viewModel: EditNursingCourseSheetViewModel = hiltViewModel(),
    patientCode: String,
    state: SheetState,
    homeCareCourse: HomeCareCourse?,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        LaunchedEffect(Unit) {
            run {
                if(homeCareCourse != null) {
                    viewModel.setScreenType(ScreenType.Edit(homeCareCourse))
                } else {
                    viewModel.setScreenType(ScreenType.Add)
                }
                viewModel.loadCategory()
            }
        }
        BuildSheetContent(viewModel = viewModel, patientCode, onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(
    viewModel: EditNursingCourseSheetViewModel,
    patientCode: String,
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedDate by remember {
        mutableStateOf(uiState.newNursingCourse.dateTime)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryPopup by remember {
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
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

                OutlinedTextField(
                    modifier = Modifier
                        .clickable {
                            showCategoryPopup = true
                        }
                        .fillMaxWidth(),
                    maxLines = 1,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    value = uiState.newNursingCourse.courseCategoryType?.name
                        ?: stringResource(
                            id = R.string.no_category
                        ),
                    label = {
                        Text(text = stringResource(id = R.string.category))
                    },
                    onValueChange = {

                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.id_dropdown),
                            contentDescription = ""
                        )
                    },
                    )

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
                        OutlinedTextField(
                            modifier = Modifier
                                .clickable {
                                    showDatePicker = true
                                }
                                .weight(column1Weight),
                            textStyle = MaterialTheme.typography.labelSmall,
                            value = uiState.newNursingCourse.dateTime.format("dd MMMM yyyy, HH:mm "),
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

                        Spacer(modifier = Modifier.width(24.dp))
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            maxLines = 1,
                            modifier = Modifier.weight(column2Weight),
                            value = uiState.newNursingCourse.duration?.toString() ?: "",
                            label = {
                                Text(text = stringResource(id = R.string.duration))
                            },
                            onValueChange = {
                                val duration = it.toIntOrNull()
                                if (it.isEmpty()) {
                                    viewModel.setDuration(null)
                                } else {
                                    viewModel.setDuration(duration)
                                }

                            })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.description))
                        },
                        value = uiState.newNursingCourse.description ?: "", onValueChange = {
                            viewModel.setDescription(it)
                        })
                    Spacer(modifier = Modifier.height(24.dp))

                    SwitchItem(
                        label = stringResource(id = R.string.show_in_diary),
                        enabled = true,
                        value = viewModel.getShowInDiary()
                    ) {
                        viewModel.setShowInDiary(it)
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.saveButtonClick(patientCode = patientCode)
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
        if (showCategoryPopup) {
            ListPopup(
                title = stringResource(id = R.string.category),
                items = uiState.categoriesTypes.map {
                    ListPopupItem(
                        label = it.name,
                        item = it
                    )
                } + ListPopupItem(
                    stringResource(id = R.string.no_category), item = null
                ),
                setShowDialog = {
                    showCategoryPopup = false
                },
                onItemSelected = {
                    viewModel.setCategoryId(it.item?.id)
                    showCategoryPopup = false
                })
        }
        if (uiState.isSuccess) {
            viewModel.clearState()
            onDismissRequest()
        }
    }
}

@Composable
private fun SwitchItem(
    label: String,
    value: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label
        )
        Switch(
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            checked = value,
            enabled = enabled,
            onCheckedChange = {
                onChange(it)
            })
    }
}
