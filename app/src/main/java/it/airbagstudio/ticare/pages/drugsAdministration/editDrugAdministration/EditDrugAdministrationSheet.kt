package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import android.annotation.SuppressLint
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs.NOTE_CONTENT
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.ui.theme.tertiary95
import it.airbagstudio.ticare.utils.format
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDrugAdministrationSheet(
    viewModel: EditDrugAdministrationSheetViewModel = hiltViewModel(),
    isReserve: Boolean,
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    val navController = rememberNavController()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
        containerColor = if (isReserve) tertiary95 else MaterialTheme.colorScheme.surface
    ) {

        NavHost(navController = navController, startDestination = "editSheet") {

            composable("editSheet") { entry ->
                entry.savedStateHandle.get<String>(NOTE_CONTENT)?.let{ newNote ->
                    viewModel.notesText = newNote
                }

                BuildContent(isReserve,viewModel, navController)
            }
            composable("editNoteScreen?$NOTE_CONTENT={$NOTE_CONTENT}", arguments = listOf(navArgument(NOTE_CONTENT) { nullable = true })){ entry ->
                val note = entry.arguments?.getString(NOTE_CONTENT) ?: ""
                Log.w("startingText",note)
                EditNoteScreen(startingText = note) {newText ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(NOTE_CONTENT,newText)

                    navController.popBackStack()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun BuildContent(isReserve: Boolean,viewModel: EditDrugAdministrationSheetViewModel,navController: NavController) {
    val focusManager = LocalFocusManager.current
    var selectedDate by remember {
        mutableStateOf(Date())
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
            text = "Meto Zeroch cpr ret 25mg",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.padding(top = 24.dp)
        ) {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.weight(1f),
                value = "2",
                onValueChange = {},
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
                value = "2",
                enabled = false,
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.prescribed)) }
            )
            Spacer(modifier = Modifier.width(24.dp))
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.weight(1f),
                value = "2",
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.duration)) }
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .clickable {
                    showDatePicker = true
                }
                .fillMaxWidth()
                .padding(top = 24.dp),
            value = selectedDate.format("dd MMMM yyyy, HH:mm "),
            enabled = false,
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
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
        Box(modifier = Modifier.height(500.dp)) {
            if (showDatePicker) {
                Column() {
                    DatePicker(
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
                                showTimePicker = false
                                showDatePicker = false
                            }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                }

            } else {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    NotesButton(
                        text = viewModel.notesText,
                    ){
                        navController.navigate("editNoteScreen?$NOTE_CONTENT=${Uri.encode(viewModel.notesText)}")
                    }

                    SwitchItem(
                        label = stringResource(id = R.string.show_in_diary),
                        isReserve = isReserve,
                        value = true
                    ) {

                    }
                    SwitchItem(
                        label = stringResource(id = R.string.rejected_by_patient),
                        isReserve = isReserve,
                        value = true
                    ) {

                    }
                    SwitchItem(
                        label = stringResource(id = R.string.not_performed),
                        isReserve = isReserve,
                        value = true
                    ) {

                    }
                    SwitchItem(
                        label = stringResource(id = R.string.patient_medication),
                        isReserve = isReserve,
                        value = true
                    ) {

                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReserve) MaterialTheme.colorScheme.tertiary else seed
                        ),
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.execute)
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.execute))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
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
private fun SwitchItem(
    label: String,
    isReserve: Boolean,
    value: Boolean,
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
            onCheckedChange = {
                onChange(it)
            })
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewEditDrugAdministrationSheet() {
    val viewMode = EditDrugAdministrationSheetViewModel()
    AppTheme {
        Scaffold() {
            BuildContent(isReserve = false,viewMode, NavController(
                LocalContext.current))
        }


    }
}