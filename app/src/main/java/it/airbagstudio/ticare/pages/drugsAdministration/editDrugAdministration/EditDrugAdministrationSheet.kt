package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.ClinicType
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.notesPopupButton.MultipleNotesPopupButton
import it.airbagstudio.ticare.ui.components.notesPopupButton.MultipleNotesPopupItem
import it.airbagstudio.ticare.ui.components.notesPopupButton.NotesPopupButton
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.ui.theme.tertiary95
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.getExecDateTime

@Composable
fun EditDrugAdministrationSheet(
    viewModel: EditDrugAdministrationSheetViewModel = hiltViewModel(),
    task: AgendaTask?,
    canWrite: Boolean,
    onDismissRequest: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.task.value = task?.copy(showInDiary = task.isReserve)
        viewModel.quantity.value = null
        viewModel.canWrite = canWrite
        if (task?.execDate == null && task?.isReserve == false) {
            //viewModel.task.value = viewModel.task.value?.copy(quantity = task?.expQuantity ?: 0.0)
            viewModel.setQuantity(task.expQuantity.toString())
        }
        if (task?.execDate != null) {
            viewModel.task.value = viewModel.task.value?.copy(
                showInDiary = task.showInDiary,
                isSkipped = task.isSkipped,
                rejected = task.rejected
            )
            viewModel.setQuantity(task.quantity.toString())
        }
    }


    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
    ) {

        if (viewModel.isSucces) {
            viewModel.isSucces = false
            onDismissRequest()
        }
        BuildContent(viewModel, onDismissRequest)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildContent(
    viewModel: EditDrugAdministrationSheetViewModel,
    onDismissRequest: () -> Unit
) {

    val task = viewModel.task.value
    val isReserve = task?.isReserve ?: false
    val focusManager = LocalFocusManager.current
    val isValid = task != null && (!task.isSkipped || task.notes.isNotEmpty())
    val clinicTypeState = viewModel.clinicType.collectAsState(initial = ClinicType.SPITEX)

    Scaffold(
        containerColor = if (task?.isReserve == true) tertiary95 else MaterialTheme.colorScheme.surface,

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(text = task?.itemDescription ?: "")
                    }

                },
                actions = {
                    IconButton(onClick = { onDismissRequest() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                }
            )
        }
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                OutlinedTextField(
                    enabled = viewModel.isEditingEnable.invoke(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f),
                    value = viewModel.quantity.value ?: "",
                    visualTransformation = if (viewModel.quantity.value.isNullOrEmpty()) PlaceholderTransformation(
                        "0"
                    ) else VisualTransformation.None,
                    onValueChange = {
                        viewModel.setQuantity(it)
                    },
                    label = { Text(text = stringResource(id = R.string.quantity)) },
                    trailingIcon = {
                        clinicTypeState.value?.let {
                            when(it){
                                ClinicType.CPA ->{
                                    Text(
                                        text = task?.itemMsmUnit ?: "",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                else ->{}
                            }
                        }
                    }
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
                    label = { Text(text = stringResource(id = R.string.prescribed)) },
                    trailingIcon = {
                        clinicTypeState.value?.let {
                            when(it){
                                ClinicType.CPA ->{
                                    Text(
                                        text = task?.itemMsmUnit ?: "",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                else ->{}
                            }
                        }
                    }
                )
                val duration = if ((viewModel.task.value?.duration
                        ?: 0) > 0
                ) "${viewModel.task.value?.duration}" else ""
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
            Spacer(modifier = Modifier.height(24.dp))
            CalendarTextField(
                enabled = viewModel.isEditingEnable.invoke(),
                modifier = Modifier.fillMaxWidth(),
                date = task?.getExecDateTime(),
                label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                onDateChanged = {
                    viewModel.setExecutedDate(it)
                })
            Column(modifier = Modifier.padding(top = 16.dp)) {
                MultipleNotesPopupButton(notes = listOf(
                    MultipleNotesPopupItem(
                        title = stringResource(id = R.string.general_note),
                        text = task?.schedulerNotes
                    ),
                    MultipleNotesPopupItem(
                        title = stringResource(id = R.string.scheduling_notes),
                        text = task?.sysSchedulingNotes
                    )
                ))

                NotesPopupButton(
                    text = task?.notes ?: "",
                    enabled = viewModel.isEditingEnable.invoke(),
                    editable = true,
                    onTextChanged = {
                        viewModel.setNote(it)
                    })
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
                Button(
                    enabled = !viewModel.isLoading && viewModel.isEditingEnable.invoke() && isValid,
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
                    Text(text = stringResource(id = R.string.save))
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


    if (viewModel.errorMessage != null) {
        ErrorAlert(
            message = viewModel.errorMessage ?: stringResource(id = R.string.generic_error_message),
            onDismissRequest = {
                viewModel.errorMessage = null
            })
    }
    if (viewModel.messagesStringIdentifiers?.isNotEmpty() == true) {
        ErrorAlert(
            message = viewModel.messagesStringIdentifiers?.map { stringResource(id = it) }
                ?.joinToString("\n") ?: "",
            onDismissRequest = {
                viewModel.messagesStringIdentifiers = null
            })
    }

}

@Composable
private fun NotesButton(text: String, enabled: Boolean, onClick: () -> Unit) {
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
private fun SchedulingNoteButton(text: String, onClick: () -> Unit) {
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