package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingType
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.RadioGroupScale
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.SeniorSittingTypeSelector
import it.airbagstudio.ticare.ui.theme.formColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeniorSittingFormScreen(
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    formRepository: FormRepository
) {
    val viewModel: SeniorSittingFormViewModel = viewModel(
        factory = it.airbagstudio.ticare.pages.patientDetails.form.ui.factories.SeniorSittingFormViewModelFactory(formRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formId) {
        viewModel.initForm(formId)
    }

    LaunchedEffect(uiState) {
        if (uiState is SeniorSittingFormUiState.Saved) {
            onSaved((uiState as SeniorSittingFormUiState.Saved).savedForm.id)
        }
    }

    // Show validation errors
    LaunchedEffect(uiState) {
        val state = uiState as? SeniorSittingFormUiState.Editing ?: return@LaunchedEffect
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
                        if (currentState is SeniorSittingFormUiState.Editing) {
                            SeniorSittingQuestions.getFormTitle(currentState.form.type)
                        } else {
                            "Senior Sitting"
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
            is SeniorSittingFormUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SeniorSittingFormUiState.Error -> {
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
            is SeniorSittingFormUiState.Editing -> {
                SeniorSittingFormContent(
                    modifier = Modifier.padding(paddingValues),
                    editingState = state,
                    viewModel = viewModel
                )
            }
            is SeniorSittingFormUiState.Saved -> {
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
fun SeniorSittingFormContent(
    modifier: Modifier = Modifier,
    editingState: SeniorSittingFormUiState.Editing,
    viewModel: SeniorSittingFormViewModel
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Type Selector
        SeniorSittingTypeSelector(
            selectedType = form.type,
            onTypeSelected = { viewModel.onTypeChanged(it) },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Patient Data Section
        SeniorSittingPatientDataSection(
            patientData = form.patientData,
            compilationTimestamp = form.compilationTimestamp,
            onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
            isPatientDataSectionInvalid = !editingState.isCompilationTimestampValid,
            compilationTimestampError = if (!editingState.isCompilationTimestampValid) "Campo richiesto" else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Form Sections
        form.sections.forEach { section ->
            Spacer(modifier = Modifier.height(if (form.sections.first() == section) 0.dp else 24.dp))
            
            SeniorSittingFormSectionRenderer(
                section = section,
                formType = form.type,
                onQuestionResponseChanged = { questionId, newScore, newText ->
                    viewModel.onQuestionResponseChanged(section.sectionId, questionId, newScore, newText)
                },
                onCheckboxChanged = { questionId, isChecked ->
                    viewModel.onCheckboxChanged(section.order, questionId, isChecked)
                },
                isInvalid = editingState.invalidFieldKeys.contains(SeniorSittingFormViewModel.ValidationKeys.sectionKey(section.sectionId)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.saveForm() },
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

            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(
                text = "Data di nascita: $birthDateString",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Zona: ${patientData.zone.ifEmpty { "N/D" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
fun SeniorSittingFormSectionRenderer(
    section: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingSection,
    formType: SeniorSittingType,
    onQuestionResponseChanged: (questionId: Int, newScore: Int?, newText: String?) -> Unit,
    onCheckboxChanged: (questionId: Int, isChecked: Boolean) -> Unit,
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

            // Show introductory text if available
            section.introductoryText?.let { introText ->
                Text(
                    text = introText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            when (formType) {
                SeniorSittingType.ADESIONE -> {
                    when (section.sectionId) {
                        "scaled_questions" -> {
                            section.questions.forEach { question ->
                                if (question.questionId == SeniorSittingQuestions.Q7_AIUTO_SCUDO_ID) {
                                    // Q7 as text field
                                    OutlinedTextField(
                                        value = question.questionText,
                                        onValueChange = { newText ->
                                            onQuestionResponseChanged(question.questionId, null, newText)
                                        },
                                        label = { Text("In cosa Scudo potrebbe aiutarla maggiormente per farla sentire meglio e ridurre il suo carico assistenziale?") },
                                        placeholder = { Text("Scrivi qui...") },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp).defaultMinSize(minHeight = 100.dp),
                                        singleLine = false,
                                        maxLines = 5
                                    )
                                } else {
                                    // Scale questions 1-6
                                    ScaleQuestionItem(
                                        questionText = question.questionText,
                                        score = question.score,
                                        onScoreChange = { newScore ->
                                            onQuestionResponseChanged(question.questionId, newScore, question.questionText)
                                        }
                                    )
                                }
                            }
                        }
                        "management_organization_questions" -> {
                            section.questions.forEach { question ->
                                OutlinedTextField(
                                    value = question.questionText,
                                    onValueChange = { newText ->
                                        onQuestionResponseChanged(question.questionId, null, newText)
                                    },
                                    label = { Text("Suggerimenti, osservazioni:") },
                                    placeholder = { Text("Scrivi qui...") },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).defaultMinSize(minHeight = 100.dp),
                                    singleLine = false,
                                    maxLines = 5
                                )
                            }
                        }
                    }
                }
                SeniorSittingType.NON_ADESIONE -> {
                    when (section.sectionId) {
                        "non_adesione_reasons" -> {
                            section.questions.forEach { question ->
                                CheckboxQuestionItem(
                                    questionText = question.questionText,
                                    isChecked = question.score == 1,
                                    onCheckedChange = { isChecked ->
                                        onCheckboxChanged(question.questionId, isChecked)
                                    }
                                )
                            }
                        }
                        "non_adesione_suggestions" -> {
                            section.questions.forEach { question ->
                                OutlinedTextField(
                                    value = question.questionText,
                                    onValueChange = { newText ->
                                        onQuestionResponseChanged(question.questionId, null, newText)
                                    },
                                    label = { Text("Suggerimenti, osservazioni:") },
                                    placeholder = { Text("Scrivi qui...") },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).defaultMinSize(minHeight = 100.dp),
                                    singleLine = false,
                                    maxLines = 5
                                )
                            }
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

@Composable
fun CheckboxQuestionItem(
    questionText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null // null because toggleable handles it
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}