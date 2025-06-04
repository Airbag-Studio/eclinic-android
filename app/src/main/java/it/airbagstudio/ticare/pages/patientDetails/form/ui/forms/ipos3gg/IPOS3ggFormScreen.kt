package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos3gg

// DatePickerInputField is no longer directly used by IPOS3ggFormContent or the new PatientDataSection
// FormSection import removed as it's not used in the current IPOS3ggFormContent structure
// May need new components for free text lists (Q1) and mixed free text + scale (Q2b)
// and single choice radio group (Q10)
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.RadioButton
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
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOS3ggQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.RadioGroupScale
import it.airbagstudio.ticare.pages.patientDetails.form.ui.factories.IPOS3ggFormViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPOS3ggFormScreen(
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    formRepository: FormRepository // Corrected type
) {
    val viewModel: IPOS3ggFormViewModel = viewModel(
        factory = IPOS3ggFormViewModelFactory(formRepository, formId)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is IPOS3ggFormUiState.Saved) {
            onSaved((uiState as IPOS3ggFormUiState.Saved).savedForm.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_ipos3gg_title)) }, // Add to strings.xml
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.close_form))
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is IPOS3ggFormUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is IPOS3ggFormUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.error_loading_form_message, state.message))
                }
            }
            is IPOS3ggFormUiState.Editing -> {
                IPOS3ggFormContent(
                    modifier = Modifier.padding(paddingValues),
                    editingState = state,
                    viewModel = viewModel
                )
            }
            is IPOS3ggFormUiState.Saved -> {
                // Usually handled by LaunchedEffect to navigate away
                // Can show a temporary success message if needed before navigation
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.form_saved_successfully))
                }
            }
        }
    }
}

@Composable
fun IPOS3ggFormContent(
    modifier: Modifier = Modifier,
    editingState: IPOS3ggFormUiState.Editing,
    viewModel: IPOS3ggFormViewModel
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Intro Text
        Text(
            text = IPOS3ggQuestions.formIntroText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Patient Data Section (Now using the read-only version from CBI style)
        PatientDataSection(
            patientData = form.patientData,
            compilationTimestamp = form.compilationTimestamp,
            onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
            isPatientDataSectionInvalid = !editingState.isCompilationTimestampValid, // Simplified, primarily for timestamp error
            compilationTimestampError = if (!editingState.isCompilationTimestampValid) stringResource(R.string.field_required) else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        // IPOS3gg Specific Sections
        form.sections.forEach { section ->
            // Add more spacing before each section title
            Spacer(modifier = Modifier.height(if (form.sections.first() == section) 0.dp else 24.dp)) 
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium), // Enhanced section title
                    modifier = Modifier.padding(bottom = 12.dp) // Increased bottom padding for title
                )

                when (section.sectionId) {
                    "Q1" -> { // Problemi o preoccupazioni principali (3 free text)
                        section.questions.forEachIndexed { index, question ->
                            OutlinedTextField(
                                value = question.questionText, // User input is stored here
                                onValueChange = { newText ->
                                    viewModel.onQuestionResponseChanged(
                                        sectionId = section.sectionId,
                                        questionId = question.questionId,
                                        newScore = null, // No score for Q1 items
                                        newText = newText
                                    )
                                },
                                label = { Text(stringResource(R.string.form_ipos3gg_q1_concern_label, index + 1), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), // Increased spacing
                                singleLine = false,
                                maxLines = 3
                            )
                        }
                    }
                    "Q2" -> { // Sintomi disturbanti (0-4 scale)
                        section.questions.forEach { question ->
                            ScaleQuestionItem(
                                questionText = question.questionText, // This is the symptom name
                                score = question.score,
                                onScoreChange = { newScore -> // newScore is Int? from ScaleQuestionItem
                                    viewModel.onQuestionResponseChanged(
                                        sectionId = section.sectionId,
                                        questionId = question.questionId,
                                        newScore = newScore,
                                        newText = question.questionText // Text (symptom name) doesn't change
                                    )
                                }
                            )
                        }
                    }
                    "Q2b" -> { // Sintomi aggiuntivi (free text + 0-4 scale)
                        Text(
                            text = stringResource(R.string.form_ipos3gg_q2b_intro),
                            style = MaterialTheme.typography.bodyMedium, // Enhanced style
                            modifier = Modifier.padding(bottom = 12.dp) // Increased spacing
                        )
                        section.questions.forEachIndexed { index, question ->
                            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) { // Spacing similar to ScaleQuestionItem
                                OutlinedTextField(
                                    value = question.questionText, // User-entered symptom name
                                    onValueChange = { newText ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = question.score, // Keep current score for this QuestionResponse object
                                            newText = newText
                                        )
                                    },
                                    label = { Text(stringResource(R.string.form_ipos3gg_q2b_symptom_label, index + 1)) }, // Style was already applied
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Space before its scale
                                    singleLine = true
                                )
                                RadioGroupScale(
                                    selectedValue = question.score,
                                    onValueSelected = { newScoreValue ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = newScoreValue,
                                            newText = question.questionText // Keep current text for this QuestionResponse object
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth() // Make scale take full width
                                )
                            }
                        }
                    }
                    "Q3_Q9" -> { // Stato emotivo, relazionale, spirituale e pratico (0-4 scale)
                        section.questions.forEach { question ->
                            ScaleQuestionItem(
                                questionText = question.questionText, // This is the full question prompt
                                score = question.score,
                                onScoreChange = { newScore -> // newScore is Int? from ScaleQuestionItem
                                    viewModel.onQuestionResponseChanged(
                                        sectionId = section.sectionId,
                                        questionId = question.questionId,
                                        newScore = newScore,
                                        newText = question.questionText // Text (question prompt) doesn't change
                                    )
                                }
                            )
                        }
                    }
                    "Q10" -> { // Modalità di compilazione (single choice)
                        val q10Response = section.questions.firstOrNull { it.questionId == IPOS3ggQuestions.Q10_MODALITA_COMPILAZIONE_ID }
                        IPOS3ggQuestions.q10Options.forEachIndexed { index, optionText ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp) // Increased vertical padding
                                    .clickable { // Make whole row clickable
                                        q10Response?.let {
                                            viewModel.onQuestionResponseChanged(
                                                sectionId = section.sectionId,
                                                questionId = it.questionId,
                                                newScore = index,
                                                newText = null
                                            )
                                        }
                                    }
                            ) {
                                RadioButton(
                                    selected = (q10Response?.score == index),
                                    onClick = { // onClick for RadioButton itself
                                        q10Response?.let {
                                            viewModel.onQuestionResponseChanged(
                                                sectionId = section.sectionId,
                                                questionId = it.questionId,
                                                newScore = index,
                                                newText = null
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = optionText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp)) // Divider between sections
        }
        
        // Closing Message
        Text(
            text = IPOS3ggQuestions.formClosingMessage,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

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
        Spacer(modifier = Modifier.height(32.dp)) // For bottom padding
    }
}

@Composable
fun PatientDataSection( // Copied and adapted from CbiFormScreen.kt
    patientData: PatientData,
    compilationTimestamp: Long,
    onCompilationDateTimeSelected: (Long) -> Unit,
    isPatientDataSectionInvalid: Boolean = false, // For border, can be adapted from IPOS state
    compilationTimestampError: String? = null // For error message under DateTimePicker
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Using a standard color, or use formColors if defined and imported
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

            // Static display for Patient Name and Surname
            Text(
                text = "${stringResource(R.string.label_first_name)}: ${patientData.name.ifEmpty { "N/D" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${stringResource(R.string.label_last_name)}: ${patientData.surname.ifEmpty { "N/D" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Static display for Patient Birth Date
            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(
                text = "${stringResource(R.string.birth_date)}: $birthDateString",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp) 
            )

            // Editable Compilation Timestamp
            DateTimePickerInputField(
                label = stringResource(R.string.compilation_date_time),
                selectedTimestamp = compilationTimestamp,
                onTimestampSelected = onCompilationDateTimeSelected,
                error = compilationTimestampError,
                modifier = Modifier.fillMaxWidth(),
                enabled = true // This should be editable
            )
        }
    }
}

// TODO: Add string resources to strings.xml:
// R.string.form_ipos3gg_q1_concern_label -> "Problema/Preoccupazione %1$d"
// R.string.form_ipos3gg_q2b_intro -> "Elenca fino a 3 altri sintomi o problemi che ti hanno disturbato e indicali:"
// R.string.form_ipos3gg_q2b_symptom_label -> "Sintomo aggiuntivo %1$d"
// (Plus the ones already listed: form_ipos3gg_title, patient_data_section_title, birth_date, compilation_date_time)
// (And ensure common ones like error_loading_form_message, close_form, save_and_submit, etc. exist)

@Composable
fun ScaleQuestionItem(
    questionText: String,
    score: Int?,
    onScoreChange: (Int?) -> Unit, // This callback expects Int?
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) { // Increased bottom padding for spacing between scale questions
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), // More prominent question text
            modifier = Modifier.padding(bottom = 8.dp) // Increased spacing between question and scale
        )
        RadioGroupScale(
            selectedValue = score, // Corrected parameter
            onValueSelected = { nonNullableScore -> onScoreChange(nonNullableScore) }, // Corrected parameter & adapt to Int?
            modifier = Modifier.fillMaxWidth()
        )
    }
}
