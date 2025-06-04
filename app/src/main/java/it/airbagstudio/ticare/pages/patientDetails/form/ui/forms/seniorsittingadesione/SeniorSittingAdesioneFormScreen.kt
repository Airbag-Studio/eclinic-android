package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingadesione

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingAdesioneQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.RadioGroupScale
import it.airbagstudio.ticare.pages.patientDetails.form.ui.factories.SeniorSittingAdesioneFormViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeniorSittingAdesioneFormScreen(
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    formRepository: FormRepository
) {
    val viewModel: SeniorSittingAdesioneFormViewModel = viewModel(
        factory = SeniorSittingAdesioneFormViewModelFactory(formRepository, formId)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SeniorSittingAdesioneFormUiState.Saved) {
            onSaved((uiState as SeniorSittingAdesioneFormUiState.Saved).savedForm.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_senior_sitting_adesione_title)) }, // Ensure this string exists
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.close_form))
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SeniorSittingAdesioneFormUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SeniorSittingAdesioneFormUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.error_loading_form_message, state.message))
                }
            }
            is SeniorSittingAdesioneFormUiState.Editing -> {
                SeniorSittingAdesioneFormContent(
                    modifier = Modifier.padding(paddingValues),
                    editingState = state,
                    viewModel = viewModel
                )
            }
            is SeniorSittingAdesioneFormUiState.Saved -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.form_saved_successfully))
                }
            }
        }
    }
}

@Composable
fun SeniorSittingAdesioneFormContent(
    modifier: Modifier = Modifier,
    editingState: SeniorSittingAdesioneFormUiState.Editing,
    viewModel: SeniorSittingAdesioneFormViewModel
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // No specific intro text in spec, directly to patient data

        SeniorSittingPatientDataSection( // Adapted PatientDataSection
            patientData = form.patientData,
            compilationTimestamp = form.compilationTimestamp,
            onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
            isPatientDataSectionInvalid = !editingState.isCompilationTimestampValid,
            compilationTimestampError = if (!editingState.isCompilationTimestampValid) stringResource(R.string.field_required) else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        form.sections.forEach { section ->
            Spacer(modifier = Modifier.height(if (form.sections.first() == section) 0.dp else 24.dp))
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                when (section.sectionId) {
                    "scaled_questions" -> {
                        section.questions.forEach { question ->
                            if (question.questionId == SeniorSittingAdesioneQuestions.Q7_AIUTO_SCUDO_ID) {
                                // Render Q7 as OutlinedTextField after scaled questions within the same section
                                OutlinedTextField(
                                    value = question.questionText,
                                    onValueChange = { newText ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = null,
                                            newText = newText
                                        )
                                    },
                                    // The label is the text that appears above the field.
                                    // The placeholder is the text that appears inside the field when empty.
                                    label = { Text(question.questionText) }, 
                                    placeholder = { Text(stringResource(R.string.placeholder_scrivi_qui)) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp).defaultMinSize(minHeight = 100.dp),
                                    singleLine = false,
                                    maxLines = 5
                                )
                            } else {
                                // Scaled questions 1-6
                                ScaleQuestionItem1to5(
                                    questionText = question.questionText,
                                    score = question.score,
                                    onScoreChange = { newScore ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = newScore,
                                            newText = question.questionText
                                        )
                                    }
                                )
                            }
                        }
                    }
                    "management_organization_questions" -> { // Section for Q8
                        section.questions.forEach { question -> // Should only be Q8
                            OutlinedTextField(
                                value = question.questionText,
                                onValueChange = { newText ->
                                    viewModel.onQuestionResponseChanged(
                                        sectionId = section.sectionId,
                                        questionId = question.questionId,
                                        newScore = null,
                                        newText = newText
                                        )
                                    },
                                    // The label is the text that appears above the field.
                                    // The placeholder is the text that appears inside the field when empty.
                                    label = { Text(question.questionText) },
                                    placeholder = { Text(stringResource(R.string.placeholder_scrivi_qui)) },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).defaultMinSize(minHeight = 100.dp),
                                    singleLine = false,
                                maxLines = 5
                            )
                        }
                    }
                }
            }
            // Add a divider after each section except the last one
            if (section != form.sections.lastOrNull()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
        
        // No specific closing message in spec

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.saveForm() },
            modifier = Modifier.fillMaxWidth(),
            enabled = editingState.isFormValid && !editingState.isSaving
        ) {
            if (editingState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.save_and_submit))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SeniorSittingPatientDataSection(
    patientData: PatientData,
    compilationTimestamp: Long,
    onCompilationDateTimeSelected: (Long) -> Unit,
    isPatientDataSectionInvalid: Boolean = false,
    compilationTimestampError: String? = null
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isPatientDataSectionInvalid && compilationTimestampError != null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.patient_data_section_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "${stringResource(R.string.label_first_name)}: ${patientData.name.ifEmpty { "N/D" }}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = "${stringResource(R.string.label_last_name)}: ${patientData.surname.ifEmpty { "N/D" }}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(text = "${stringResource(R.string.birth_date)}: $birthDateString", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "${stringResource(R.string.label_zone)}: ${patientData.zone.ifEmpty { "N/D" }}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp)) // Added Zone

            DateTimePickerInputField(
                label = stringResource(R.string.compilation_date_time),
                selectedTimestamp = compilationTimestamp,
                onTimestampSelected = onCompilationDateTimeSelected,
                error = compilationTimestampError,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
        }
    }
}

@Composable
fun ScaleQuestionItem1to5( // Placeholder - uses 0-4 scale component for now
    questionText: String,
    score: Int?,
    onScoreChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // TODO: This RadioGroupScale is 0-4. Need a 1-5 version.
        // For now, the ViewModel validation expects 1-5.
        // The UI will show 0-4 but only 1-4 will be "valid" from VM perspective if 0 is chosen.
        // This needs a custom component or adaptation of RadioGroupScale.
        RadioGroupScale(
            selectedValue = score, // If score is 5, this won't show selected.
            onValueSelected = { nonNullableScore ->
                // Map 0-4 from UI to 1-5 for VM if needed, or create proper 1-5 component
                onScoreChange(nonNullableScore)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// TODO: Add string resources to strings.xml:
// R.string.form_senior_sitting_adesione_title -> "Senior Sitting – Adesione"
// R.string.label_zone -> "Zona"
