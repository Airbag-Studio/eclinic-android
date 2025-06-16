package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.IPOSFloatingLegend
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.RadioGroupScale
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.TimePeriodSelector
import it.airbagstudio.ticare.ui.theme.formColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPOSFormScreen(
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    viewModel: IPOSFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formId) {
        viewModel.initForm(formId)
    }

    LaunchedEffect(uiState) {
        if (uiState is IPOSFormUiState.Saved) {
            onSaved((uiState as IPOSFormUiState.Saved).savedForm.id)
        }
    }

    // Show validation errors
    LaunchedEffect(uiState) {
        val state = uiState as? IPOSFormUiState.Editing ?: return@LaunchedEffect
        if (state.validationErrors.isNotEmpty()) {
            snackbarHostState.showSnackbar(message = state.validationErrors.first())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val currentState = uiState
                    Text(
                        if (currentState is IPOSFormUiState.Editing) {
                            IPOSQuestions.getFormTitle(currentState.form.timePeriod)
                        } else {
                            "IPOS"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Chiudi")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is IPOSFormUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is IPOSFormUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is IPOSFormUiState.Editing -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    IPOSFormContent(
                        formId = formId,
                        editingState = state,
                        viewModel = viewModel
                    )
                    IPOSFloatingLegend(
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
            is IPOSFormUiState.Saved -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Form salvato con successo!")
                }
            }
        }
    }
}

@Composable
fun IPOSFormContent(
    formId: String?,
    editingState: IPOSFormUiState.Editing,
    viewModel: IPOSFormViewModel
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(top = 64.dp) // Add top padding to account for floating legend
    ) {
        // Time Period Selector
        TimePeriodSelector(
            selectedPeriod = form.timePeriod,
            onPeriodSelected = { viewModel.onTimePeriodChanged(it) },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Intro Text
        Text(
            text = form.timePeriod.introText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Patient Data Section
        PatientDataSection(
            patientData = form.patientData,
            compilationTimestamp = form.compilationTimestamp,
            onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
            isPatientDataSectionInvalid = !editingState.isCompilationTimestampValid,
            compilationTimestampError = if (!editingState.isCompilationTimestampValid) "Campo richiesto" else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        // IPOS Sections
        form.sections.forEach { section ->
            Spacer(modifier = Modifier.height(if (form.sections.first() == section) 0.dp else 24.dp))
            
            IPOSFormSectionRenderer(
                section = section,
                timePeriod = form.timePeriod,
                onQuestionResponseChanged = { questionId, newScore, newText ->
                    viewModel.onQuestionResponseChanged(section.sectionId, questionId, newScore, newText)
                },
                isInvalid = editingState.invalidFieldKeys.contains(IPOSFormViewModel.ValidationKeys.sectionKey(section.sectionId)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Closing Message
        Text(
            text = IPOSQuestions.formClosingMessage,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.saveForm(formId) },
            modifier = Modifier.fillMaxWidth(),
            enabled = editingState.isFormValid && !editingState.isSaving
        ) {
            if (editingState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Salva e Invia")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PatientDataSection(
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
            containerColor = MaterialTheme.formColors.sectionBackground
        ),
        border = if (isPatientDataSectionInvalid && compilationTimestampError != null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
             
            Text(
                text = "Dati Paziente",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Static display for Patient Name and Surname
            Text(
                text = "Nome: ${patientData.name.ifEmpty { "N/D" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Cognome: ${patientData.surname.ifEmpty { "N/D" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Static display for Patient Birth Date
            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(
                text = "Data di nascita: $birthDateString",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Editable Compilation Timestamp
            DateTimePickerInputField(
                label = "Data e ora compilazione",
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
fun IPOSFormSectionRenderer(
    section: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSSection,
    timePeriod: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSTimePeriod,
    onQuestionResponseChanged: (questionId: Int, newScore: Int?, newText: String?) -> Unit,
    isInvalid: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.formColors.sectionBackground),
        border = if (isInvalid) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (section.sectionId) {
                "Q1" -> {
                    Text(
                        text = timePeriod.q1IntroText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    section.questions.forEachIndexed { index, question ->
                        OutlinedTextField(
                            value = question.questionText,
                            onValueChange = { newText ->
                                onQuestionResponseChanged(question.questionId, null, newText)
                            },
                            label = { Text("Problema/Preoccupazione ${index + 1}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = false,
                            maxLines = 3
                        )
                    }
                }
                "Q2" -> {
                    Text(
                        text = timePeriod.q2IntroText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    section.questions.forEach { question ->
                        ScaleQuestionItem(
                            questionText = question.questionText,
                            score = question.score,
                            onScoreChange = { newScore ->
                                onQuestionResponseChanged(question.questionId, newScore, question.questionText)
                            }
                        )
                    }
                }
                "Q2b" -> {
                    Text(
                        text = "Elenca fino a 3 altri sintomi o problemi che ti hanno disturbato e indicali:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    section.questions.forEachIndexed { index, question ->
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            OutlinedTextField(
                                value = question.questionText,
                                onValueChange = { newText ->
                                    onQuestionResponseChanged(question.questionId, question.score, newText)
                                },
                                label = { Text("Sintomo aggiuntivo ${index + 1}") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true
                            )
                            RadioGroupScale(
                                selectedValue = question.score,
                                onValueSelected = { newScoreValue ->
                                    onQuestionResponseChanged(question.questionId, newScoreValue, question.questionText)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                "Q3_Q9" -> {
                    section.questions.forEach { question ->
                        ScaleQuestionItem(
                            questionText = question.questionText,
                            score = question.score,
                            onScoreChange = { newScore ->
                                onQuestionResponseChanged(question.questionId, newScore, question.questionText)
                            }
                        )
                    }
                }
                "Q10" -> {
                    val q10Response = section.questions.firstOrNull { it.questionId == IPOSQuestions.Q10_MODALITA_COMPILAZIONE_ID }
                    IPOSQuestions.q10Options.forEachIndexed { index, optionText ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    q10Response?.let {
                                        onQuestionResponseChanged(it.questionId, index, null)
                                    }
                                }
                        ) {
                            RadioButton(
                                selected = (q10Response?.score == index),
                                onClick = {
                                    q10Response?.let {
                                        onQuestionResponseChanged(it.questionId, index, null)
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
    }
}

@Composable
fun ScaleQuestionItem(
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