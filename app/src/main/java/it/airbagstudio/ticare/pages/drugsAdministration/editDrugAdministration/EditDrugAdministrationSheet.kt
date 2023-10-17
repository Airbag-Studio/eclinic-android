package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs.NOTE_CONTENT
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.ui.theme.tertiary95
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getExecDateTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDrugAdministrationSheet(
    viewModel: EditDrugAdministrationSheetViewModel = hiltViewModel(),
    state: SheetState,
    task: AgendaPharmacologicalTask?,
    onDismissRequest: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.task.value = task?.copy(showInDiary = task.isReserve)
        if (task?.execDate == null && task?.isReserve == false) {
            viewModel.task.value = viewModel.task.value?.copy(quantity = task?.expQuantity ?: 0)
        }
    }
    val navController = rememberNavController()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
        containerColor = if (task?.isReserve == true) tertiary95 else MaterialTheme.colorScheme.surface
    ) {

        if (viewModel.isSucces) {
            viewModel.isSucces = false
            onDismissRequest()
        }
        NavHost(navController = navController, startDestination = "editSheet") {

            composable("editSheet") { entry ->
                entry.savedStateHandle.get<String>(NOTE_CONTENT)?.let { newNote ->
                    viewModel.task.value?.notes = newNote
                }

                BuildContent(viewModel, navController)
            }
            composable(
                "editNoteScreen?$NOTE_CONTENT={$NOTE_CONTENT}",
                arguments = listOf(navArgument(NOTE_CONTENT) { nullable = true })
            ) { entry ->
                val note = entry.arguments?.getString(NOTE_CONTENT) ?: ""
                Log.w("startingText", note)
                EditNoteScreen(startingText = note) { newText ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        NOTE_CONTENT,
                        newText
                    )

                    navController.popBackStack()
                }
            }
            composable(
                "noteScreen?$NOTE_CONTENT={$NOTE_CONTENT}",
                arguments = listOf(navArgument(NOTE_CONTENT) { nullable = true })
            ) { entry ->
                val note = entry.arguments?.getString(NOTE_CONTENT) ?: ""
                Log.w("startingText", note)
                NoteScreen(text = note) {
                    navController.popBackStack()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun BuildContent(
    viewModel: EditDrugAdministrationSheetViewModel,
    navController: NavController
) {
    viewModel.task.value?.let { task ->
        val isReserve = task.isReserve
        val focusManager = LocalFocusManager.current
        var selectedDate by remember {
            mutableStateOf(task.getExecDateTime() ?: Date())
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )
        val timePickerState = rememberTimePickerState(
            initialHour = selectedDate.hours,
            initialMinute = selectedDate.minutes
        )
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = task.itemDescription,
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                val value = if ((viewModel.task.value?.quantity ?:0) > 0) "${viewModel.task.value?.quantity}" else ""
                OutlinedTextField(
                    enabled = viewModel.isEditingEnable.invoke(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    value = value,
                    visualTransformation = if (value.isEmpty()) PlaceholderTransformation("0") else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setQuantity(it.toIntOrNull())
                    },
                    label = { Text(text = stringResource(id = R.string.quantity)) }
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    value = "${viewModel.task.value?.maxQuantity ?: 0}",
                    enabled = false,
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.prescribed)) }
                )
                val duration = if ((viewModel.task.value?.duration ?: 0) > 0) "${viewModel.task.value?.duration}" else ""
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    enabled = viewModel.isEditingEnable.invoke(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    value = duration,
                    visualTransformation = if (duration.isEmpty()) PlaceholderTransformation("0") else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setDuration(it.toIntOrNull())
                    },
                    label = { Text(text = stringResource(id = R.string.duration)) }
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .clickable {
                        if (viewModel.isEditingEnable.invoke()) {
                            showDatePicker = true
                        }
                    }
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                value = selectedDate.format("dd MMMM yyyy, HH:mm "),
                enabled = false,
                singleLine = true,
                colors = if (viewModel.isEditingEnable.invoke()) OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) else OutlinedTextFieldDefaults.colors(),
                onValueChange = {},
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_field_calendar),
                        stringResource(id = R.string.actual_date_time)
                    )
                },
                label = { Text(text = stringResource(id = R.string.actual_date_time)) }
            )
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
                                selectedDayContainerColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                selectedYearContainerColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                containerColor = if (isReserve) tertiary95 else MaterialTheme.colorScheme.surfaceVariant,
                                currentYearContentColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        )
                        Row() {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                ),
                                onClick = { showDatePicker = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                            TextButton(
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
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
                                periodSelectorSelectedContainerColor = if (isReserve) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                                selectorColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                timeSelectorSelectedContainerColor = if (isReserve) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                            )
                        )
                        Row() {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
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
                                    contentColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                ),
                                onClick = {
                                    selectedDate =
                                        if (datePickerState.selectedDateMillis != null) Date(
                                            datePickerState.selectedDateMillis!!
                                        ) else Date()
                                    selectedDate.hours = timePickerState.hour
                                    selectedDate.minutes = timePickerState.minute
                                    viewModel.setExecutedDate(selectedDate)
                                    showTimePicker = false
                                    showDatePicker = false

                                }) {
                                Text(text = stringResource(id = R.string.ok))
                            }
                        }
                    }

                } else {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        SchedulingNoteButton(text = if(task.sysSchedulingNotes.isEmpty() ) stringResource(id = R.string.no_notes) else task.sysSchedulingNotes) {
                            navController.navigate("noteScreen?$NOTE_CONTENT=${Uri.encode(task.sysSchedulingNotes)}")
                        }
                        NotesButton(
                            text = if (task.notes.isEmpty()) stringResource(id = R.string.no_notes) else task.notes,
                            enabled = viewModel.isEditingEnable.invoke()
                        ) {
                            navController.navigate("editNoteScreen?$NOTE_CONTENT=${Uri.encode(task.notes)}")
                        }

                        SwitchItem(
                            label = stringResource(id = R.string.show_in_diary),
                            isReserve = isReserve,
                            enabled = viewModel.isEditingEnable.invoke(),
                            value = viewModel.task.value?.showInDiary ?: false
                        ) {
                            viewModel.setShowInDiary(it)
                        }
                        SwitchItem(
                            label = stringResource(id = R.string.rejected_by_patient),
                            isReserve = isReserve,
                            enabled = !isReserve && viewModel.isEditingEnable.invoke(),
                            value = viewModel.task.value?.rejected ?: false
                        ) {
                            viewModel.setRejected(it)
                        }
                        SwitchItem(
                            label = stringResource(id = R.string.not_performed),
                            isReserve = isReserve,
                            enabled = !isReserve && viewModel.isEditingEnable.invoke(),
                            value = viewModel.task.value?.isSkipped ?: false
                        ) {
                            viewModel.setNotExecuted(it)
                        }
                        /*
                        SwitchItem(
                            label = stringResource(id = R.string.patient_medication),
                            isReserve = isReserve,
                            enabled = true,
                            value = viewModel.task.value?.patientOwnedDrug ?: false
                        ) {
                            viewModel.setPatientDrug(it)
                        }
*/
                        Button(
                            enabled = !viewModel.isLoading && viewModel.isEditingEnable.invoke(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReserve) MaterialTheme.colorScheme.tertiary else seed
                            ),
                            onClick = {
                                viewModel.executeTask()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(id = R.string.execute)
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(text = if(task.getExecDateTime() != null) stringResource(id = R.string.save) else stringResource(id = R.string.execute))
                            if (viewModel.isLoading) {
                                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (viewModel.errorMessage != null) {
        ErrorAlert(
            message = viewModel.errorMessage ?: stringResource(id = R.string.generic_error_message),
            onDismissRequest = {
                viewModel.errorMessage = null
            })
    }
    if(viewModel.messagesStringIdentifiers?.isNotEmpty() == true){
        ErrorAlert(
            message = viewModel.messagesStringIdentifiers?.map { stringResource(id = it) }?.joinToString("\n") ?: "",
            onDismissRequest = {
                viewModel.messagesStringIdentifiers = null
            })
    }

}

@Composable
private fun NotesButton(text: String,enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        onClick = onClick
    ) {
        Column() {
            Row() {
                Text(
                    text = stringResource(id = R.string.notes),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = stringResource(
                        id = R.string.notes
                    )
                )

            }
            Text(
                text = text,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SchedulingNoteButton(text: String, onClick: () -> Unit){
        Column(Modifier.clickable { onClick() }) {
            Row() {
                Text(
                    text = stringResource(id = R.string.scheduling_notes),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = stringResource(
                        id = R.string.notes
                    )
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis
            )
        }
}


@Composable
private fun SwitchItem(
    label: String,
    isReserve: Boolean,
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
                checkedTrackColor = if (isReserve) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            ),
            checked = value,
            enabled = enabled,
            onCheckedChange = {
                onChange(it)
            })
    }
}

/*
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewEditDrugAdministrationSheet() {
    val viewMode = EditDrugAdministrationSheetViewModel()
    AppTheme {
        Scaffold() {
            BuildContent(task = AgendaPharmacologicalTask(
                isReserve = false,

            ),viewMode, NavController(
                LocalContext.current))
        }


    }
}

 */