package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.idpall

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IDPallQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IDPallSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DatePickerInputField
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.PatientDataSection
import kotlinx.coroutines.delay

/**
 * Schermata principale per il form ID PALL.
 * Gestisce la visualizzazione e l'interazione con il questionario ID PALL.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDPallFormScreen(
    formId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IDPallFormViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isEditingEnabled by remember { mutableStateOf(false) }
    var isFormEnabled = isEditingEnabled || formId == null


    LaunchedEffect(formId) {
        viewModel.initializeForm(formId?.toInt())
    }
    val uiState by viewModel.uiState.collectAsState()
    
    // Gestione degli stati di successo e errore
    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is IDPallFormUiState.Saved -> {
                // Mostra messaggio di successo e torna indietro dopo un breve delay
                delay(1500)
                onNavigateBack()
            }
            else -> { /* Altri stati gestiti nell'UI */ }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_idpall_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    if (formId != null) {
                        IconButton(onClick = { isEditingEnabled = !isEditingEnabled }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Torna indietro"
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val currentState = uiState) {
            is IDPallFormUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is IDPallFormUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Riprova")
                        }
                    }
                }
            }
            
            is IDPallFormUiState.Editing -> {
                IDPallFormContent(
                    enabled = isFormEnabled,
                    uiState = currentState,
                    onPatientDataChanged = { viewModel.updatePatientData(it) },
                    onQuestionResponseChanged = { questionId, isYes ->
                        viewModel.updateQuestionResponse(questionId, isYes)
                    },
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel,
                    formId = formId
                )
            }
            
            is IDPallFormUiState.Saved -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Form salvato con successo!",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contenuto principale del form ID PALL in modalità di modifica.
 */
@Composable
private fun IDPallFormContent(
    enabled: Boolean,
    uiState: IDPallFormUiState.Editing,
    formId: String?,
    onPatientDataChanged: (PatientData) -> Unit,
    onQuestionResponseChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IDPallFormViewModel
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Testo introduttivo
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = IDPallQuestions.introText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
        
        // Sezione dati paziente
        item {
            PatientDataSection(
                patientData = uiState.patientData,
                compilationTimestamp = uiState.form.compilationTimestamp,
                onCompilationDateTimeSelected = { viewModel.onCompilationDateTimeSelected(it) },
            )
        }
        
        // Sezioni del questionario
        items(uiState.form.sections) { section ->
            IDPallSectionCard(
                enabled = enabled,
                section = section,
                onQuestionResponseChanged = onQuestionResponseChanged
            )
        }
        item{
            Button(
                onClick = { viewModel.saveForm(formId?.toInt()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isFormValid && !uiState.isSaving && enabled
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Salva e Invia")
                }
            }
        }
    }
}


/**
 * Card per una sezione del questionario ID PALL.
 */
@Composable
private fun IDPallSectionCard(
    enabled: Boolean,
    section: IDPallSection,
    onQuestionResponseChanged: (Int, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titolo della sezione
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Domande della sezione
            section.questions.forEach { question ->
                IDPallQuestionRenderer(
                    enabled = enabled,
                    question = question,
                    onQuestionResponseChanged = onQuestionResponseChanged
                )
            }
        }
    }
}

/**
 * Renderer per le domande ID PALL con gestione speciale per Q2 e sub-domande.
 */
@Composable
private fun IDPallQuestionRenderer(
    enabled: Boolean,
    question: QuestionResponse,
    onQuestionResponseChanged: (Int, Boolean) -> Unit
) {
    when (question.questionId) {
        2 -> {
            // Q2: Render come testo informativo + istruzione
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.select_applicable_options),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        in 21..24 -> {
            // Q2A-Q2D: Render come sub-domande indentate
            IDPallQuestionItem(
                enabled = enabled,
                question = question,
                isSubQuestion = true,
                onResponseSelected = { isYes ->
                    onQuestionResponseChanged(question.questionId, isYes)
                }
            )
        }
        
        else -> {
            // Domande normali
            IDPallQuestionItem(
                enabled = enabled,
                question = question,
                isSubQuestion = false,
                onResponseSelected = { isYes ->
                    onQuestionResponseChanged(question.questionId, isYes)
                }
            )
        }
    }
}

/**
 * Componente per una singola domanda ID PALL con opzioni Sì/No.
 */
@Composable
private fun IDPallQuestionItem(
    enabled: Boolean,
    question: QuestionResponse,
    isSubQuestion: Boolean = false,
    onResponseSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            start = if (isSubQuestion) 16.dp else 0.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = question.questionText,
            style = if (isSubQuestion) 
                MaterialTheme.typography.bodyMedium 
            else 
                MaterialTheme.typography.bodyLarge
        )
        
        // Radio buttons per Sì/No
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RadioButton(
                    enabled = enabled,
                    selected = question.score == 1,
                    onClick = { onResponseSelected(true) }
                )
                Text("Sì")
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RadioButton(
                    enabled = enabled,
                    selected = question.score == 0,
                    onClick = { onResponseSelected(false) }
                )
                Text("No")
            }
        }
    }
}
