package it.airbagstudio.ticare.pages.medicalDiagnoses.createEdit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.MedicalDiagnosis
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ConfirmWithNoteDialog
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.toDate
import java.util.Date
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditMedicalDiagnosisView(
    viewModel: CreateEditMedicalDiagnosisViewModel = hiltViewModel(),
    case: String,
    isEditingEnabled: Boolean,
    medicalDiagnosis: MedicalDiagnosis?,
    onDismissRequest: () -> Unit
){
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    val execDate by viewModel.openDate.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.error.collectAsStateWithLifecycle()
    val isSuccess by viewModel.isSuccess.collectAsStateWithLifecycle()
    var showCloseDialog by remember { mutableStateOf(false) }


    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.clearData()
            onDismissRequest()
        }
    }


    LaunchedEffect(Unit) {
        if (medicalDiagnosis != null) {
            viewModel.setDescription(medicalDiagnosis.desc)
            viewModel.setExecutedDate(medicalDiagnosis.openDate.toDate(
                SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
            ) ?: Date())
            viewModel.setMedicalDiagnosesId(medicalDiagnosis.id)
        }else{
            viewModel.clearData()
        }
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest()
        },
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column() {
                            Text(text = if (medicalDiagnosis != null) stringResource(R.string.edit_medical_diagnosis) else stringResource(R.string.new_medical_diagnosis))

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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    date = execDate,
                    label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                    enabled = isEditingEnabled,
                    onDateChanged = {
                        viewModel.setExecutedDate(it)
                    }
                )
                val height = max(250,screenHeight - 500)

                OutlinedTextField(
                    modifier = Modifier
                        .height((height).dp)
                        .fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = R.string.description))
                    },
                    value = description,
                    onValueChange = {
                        viewModel.setDescription(it)
                    },
                    enabled = isEditingEnabled,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && isEditingEnabled && description.isNotEmpty(),
                    onClick = {
                        if (medicalDiagnosis == null)
                            viewModel.save(case)
                        else {
                            viewModel.update(case)
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.save)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.save))
                    if (isLoading) {
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (medicalDiagnosis != null){
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && isEditingEnabled && description.isNotEmpty(),
                        onClick = {
                            showCloseDialog = true
                        }) {
                        Text(text = stringResource(id = R.string.close_medical_diagnosis))
                        if (isLoading) {
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            if (showCloseDialog) {
                ConfirmWithNoteDialog(
                    title = stringResource(id = R.string.closing_medical_diagnosis),
                    body = stringResource(id = R.string.medical_diagnosis_closing_note),
                    onDismissRequest = { confirm, message ->
                        if (confirm && message != null) {
                            viewModel.close(case = case,message)
                        }
                        showCloseDialog = false
                    })
            }
            if (errorMessage != null) {
                ErrorAlert(errorMessage!!, onDismissRequest = {
                    viewModel.clearError()
                })
            }
        }
    }
}