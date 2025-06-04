
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOS7ggQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.RadioGroupScale
import it.airbagstudio.ticare.pages.patientDetails.form.ui.factories.IPOS7ggFormViewModelFactory
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos7gg.IPOS7ggFormUiState
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos7gg.IPOS7ggFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPOS7ggFormScreen( // Function name changed
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    formRepository: FormRepository
) {
    val viewModel: IPOS7ggFormViewModel = viewModel( // ViewModel type changed
        factory = IPOS7ggFormViewModelFactory(formRepository, formId) // Factory type changed
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is IPOS7ggFormUiState.Saved) { // UiState type changed
            onSaved((uiState as IPOS7ggFormUiState.Saved).savedForm.id) // UiState type changed
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_ipos7gg_title)) }, // String resource changed (ensure this exists)
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.close_form))
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is IPOS7ggFormUiState.Loading -> { // UiState type changed
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is IPOS7ggFormUiState.Error -> { // UiState type changed
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.error_loading_form_message, state.message))
                }
            }
            is IPOS7ggFormUiState.Editing -> { // UiState type changed
                IPOS7ggFormContent( // Content function call changed
                    modifier = Modifier.padding(paddingValues),
                    editingState = state,
                    viewModel = viewModel
                )
            }
            is IPOS7ggFormUiState.Saved -> { // UiState type changed
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.form_saved_successfully))
                }
            }
        }
    }
}

@Composable
fun IPOS7ggFormContent( // Function name changed
    modifier: Modifier = Modifier,
    editingState: IPOS7ggFormUiState.Editing, // UiState type changed
    viewModel: IPOS7ggFormViewModel // ViewModel type changed
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = IPOS7ggQuestions.formIntroText, // Questions object changed
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PatientDataSection( // This component is reused
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
                    text = section.title, // This will come from IPOS7ggQuestions.initialSections
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                when (section.sectionId) {
                    "Q1" -> {
                        // Use IPOS7ggQuestions.q1IntroText if needed, or embed in section title
                        Text(
                            text = IPOS7ggQuestions.q1IntroText, // Added specific intro for Q1
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        section.questions.forEachIndexed { index, question ->
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
                                // Ensure R.string.form_ipos7gg_q1_concern_label exists or use a generic one
                                label = { Text(stringResource(R.string.form_ipos3gg_q1_concern_label, index + 1), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                singleLine = false,
                                maxLines = 3
                            )
                        }
                    }
                    "Q2" -> {
                         Text(
                            text = IPOS7ggQuestions.q2IntroText, // Added specific intro for Q2
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        section.questions.forEach { question ->
                            ScaleQuestionItem( // This component is reused
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
                    "Q2b" -> {
                        Text(
                            // Ensure R.string.form_ipos7gg_q2b_intro exists
                            text = stringResource(R.string.form_ipos3gg_q2b_intro),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        section.questions.forEachIndexed { index, question ->
                            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                OutlinedTextField(
                                    value = question.questionText,
                                    onValueChange = { newText ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = question.score,
                                            newText = newText
                                        )
                                    },
                                    // Ensure R.string.form_ipos7gg_q2b_symptom_label exists
                                    label = { Text(stringResource(R.string.form_ipos3gg_q2b_symptom_label, index + 1)) },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    singleLine = true
                                )
                                RadioGroupScale( // This component is reused
                                    selectedValue = question.score,
                                    onValueSelected = { newScoreValue ->
                                        viewModel.onQuestionResponseChanged(
                                            sectionId = section.sectionId,
                                            questionId = question.questionId,
                                            newScore = newScoreValue,
                                            newText = question.questionText
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    "Q3_Q9" -> {
                        section.questions.forEach { question ->
                            ScaleQuestionItem( // This component is reused
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
                    "Q10" -> {
                        val q10Response = section.questions.firstOrNull { it.questionId == IPOS7ggQuestions.Q10_MODALITA_COMPILAZIONE_ID } // Questions object changed
                        IPOS7ggQuestions.q10Options.forEachIndexed { index, optionText -> // Questions object changed
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
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
                                    onClick = {
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
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Text(
            text = IPOS7ggQuestions.formClosingMessage, // Questions object changed
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
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PatientDataSection( // Reused as is
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

            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(
                text = "${stringResource(R.string.birth_date)}: $birthDateString",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
fun ScaleQuestionItem( // Reused as is
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
        RadioGroupScale(
            selectedValue = score,
            onValueSelected = { nonNullableScore -> onScoreChange(nonNullableScore) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// TODO: Add/Update string resources to strings.xml for IPOS7gg:
// R.string.form_ipos7gg_title -> "Scala IPOS – 7gg"
// R.string.form_ipos7gg_q1_concern_label -> "Problema/Preoccupazione (7gg) %1$d" (or similar)
// R.string.form_ipos7gg_q2b_intro -> "Elenca fino a 3 altri sintomi o problemi (7gg)..." (or similar)
// R.string.form_ipos7gg_q2b_symptom_label -> "Sintomo aggiuntivo (7gg) %1$d" (or similar)
