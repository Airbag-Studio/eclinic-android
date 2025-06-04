package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid
//package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DatePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.factories.ComidFormViewModelFactory
import it.airbagstudio.ticare.ui.theme.formColors
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidFormScreen(
    formId: String? = null,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    factory: ComidFormViewModelFactory, // Pass factory
    viewModel: ComidFormViewModel = viewModel(factory = factory)
) {
    LaunchedEffect(formId) {
        viewModel.initForm(formId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is ComidFormUiState.Saved) {
            onSaved()
        }
    }
    
    // TODO: Show validation errors if ComidFormUiState.Editing includes error messages
    // LaunchedEffect(uiState) {
    //     val state = uiState as? ComidFormUiState.Editing ?: return@LaunchedEffect
    //     if (state.validationErrors.isNotEmpty()) { // Assuming validationErrors list
    //         snackbarHostState.showSnackbar(message = state.validationErrors.first())
    //     }
    // }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.comid_form_title)) }, // Use new string resource
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val currentState = uiState) {
            is ComidFormUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
            is ComidFormUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ComidFormUiState.Editing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.comid_form_title_long), // Full title
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.comid_intro_text), // Intro text
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ComidPatientDataSection(
                        patientData = currentState.patientData,
                        compilationTimestamp = currentState.form.compilationTimestamp,
                        onBirthDateSelected = viewModel::onBirthDateSelected,
                        onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
                        isBirthDateInvalid = !currentState.isBirthDateValid
                        // Add error for compilationTimestamp if needed
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    currentState.form.sections.forEach { section ->
                        ComidFormSectionRenderer(
                            section = section,
                            onQuestionResponseChanged = { questionId, response ->
                                viewModel.updateQuestionResponse(section.order, questionId, response)
                            },
                            // isInvalid = currentState.invalidFieldKeys.contains(ComidFormViewModel.ValidationKeys.sectionKey(section.order)),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.saveForm() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !currentState.isSaving
                    ) {
                        if (currentState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(R.string.save_and_submit))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            is ComidFormUiState.Saved -> {
                // Navigation is handled by the LaunchedEffect observing this state.
                // No specific UI needed here, or a brief "Saved" message could be shown
                // if not navigating immediately. For now, empty as per plan.
            }
        }
    }
}

@Composable
fun ComidPatientDataSection(
    patientData: PatientData,
    compilationTimestamp: Long,
    onBirthDateSelected: (Long?) -> Unit,
    onCompilationDateTimeSelected: (Long) -> Unit,
    isBirthDateInvalid: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.formColors.sectionBackground),
        border = if (isBirthDateInvalid) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null // Highlight whole card if birth date is invalid
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(R.string.patient_data),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Static display for Patient Name and Surname (as per CBI form)
            Text(
                text = "${stringResource(R.string.patient_name_label_static)}: ${patientData.name}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${stringResource(R.string.patient_surname_label_static)}: ${patientData.surname}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DatePickerInputField(
                label = stringResource(R.string.patient_birth_date_label),
                selectedDateMillis = patientData.birthDate, // Corrected parameter name
                onDateSelected = { millis -> onBirthDateSelected(millis) }, // Corrected parameter name and lambda
                isError = isBirthDateInvalid, // Corrected parameter name
                hintText = stringResource(R.string.select_birth_date_hint), // Added hintText
                errorTextResId = if (isBirthDateInvalid) R.string.field_required else null, // Pass error resource id
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            DateTimePickerInputField(
                label = stringResource(R.string.compilation_date_time_label),
                selectedTimestamp = compilationTimestamp,
                onTimestampSelected = onCompilationDateTimeSelected, // This component uses these names
                error = null, // DateTimePickerInputField expects 'error: String?'
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ComidFormSectionRenderer(
    section: COMIDSection,
    onQuestionResponseChanged: (questionId: Int, response: Boolean) -> Unit,
    // isInvalid: Boolean, // For highlighting section if it has errors
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.formColors.sectionBackground),
        // border = if (isInvalid) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = section.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            section.questions.forEach { question ->
                ComidQuestionItem(
                    question = question,
                    onResponseSelected = { response ->
                        onQuestionResponseChanged(question.questionId, response)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ComidQuestionItem(
    question: QuestionResponse,
    onResponseSelected: (Boolean) -> Unit
) {
    Column {
        Text(text = question.questionText, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val yesSelected = question.score == 1
            val noSelected = question.score == 0

            Row(
                Modifier
                    .selectable(
                        selected = yesSelected,
                        onClick = { onResponseSelected(true) },
                        role = Role.RadioButton
                    )
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = yesSelected, onClick = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.yes))
            }

            Row(
                Modifier
                    .selectable(
                        selected = noSelected,
                        onClick = { onResponseSelected(false) },
                        role = Role.RadioButton
                    )
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = noSelected, onClick = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.no))
            }
        }
    }
}
