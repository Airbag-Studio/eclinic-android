package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos

import android.R.attr.label
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
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
    var isEditingEnabled by remember { mutableStateOf(false) }
    var isFormEnabled = isEditingEnabled || formId == null
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
                actions = {
                    if (formId != null) {
                        IconButton(onClick = { isEditingEnabled = !isEditingEnabled }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Torna indietro"
                            )
                        }
                    }
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
                IPOSFormContent(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    enabled = isFormEnabled,
                    formId = formId,
                    editingState = state,
                    viewModel = viewModel
                )
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
    modifier: Modifier = Modifier,
    enabled: Boolean,
    formId: String?,
    editingState: IPOSFormUiState.Editing,
    viewModel: IPOSFormViewModel
) {
    val form = editingState.form
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Time Period Selector
        TimePeriodSelector(
            enabled = enabled,
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
            compilationTimestampError = if (!editingState.isCompilationTimestampValid) "Campo richiesto" else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        // IPOS Sections
        form.sections.forEach { section ->
            Spacer(modifier = Modifier.height(if (form.sections.first() == section) 0.dp else 24.dp))
            
            IPOSFormSectionRenderer(
                enabled = enabled,
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
            enabled = editingState.isFormValid && !editingState.isSaving && enabled
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
    compilationTimestampError: String? = null
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.formColors.sectionBackground
        ),
        border = null
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
    enabled: Boolean,
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
                            enabled = enabled,
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
                    
                    // Mostra la legenda dettagliata una sola volta all'inizio della sezione Q2
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LegendItem("0", "No per\nniente")
                            LegendItem("1", "Leggermente")
                            LegendItem("2", "Moderatamente")
                            LegendItem("3", "In Modo\nSevero")
                            LegendItem("4", "In modo\nintollerab.")
                        }
                    }
                    
                    section.questions.forEach { question ->
                        ScaleQuestionItem(
                            enabled = enabled,
                            questionText = question.questionText,
                            score = question.score,
                            onScoreChange = { newScore ->
                                onQuestionResponseChanged(question.questionId, newScore, question.questionText)
                            },
                            showLegend = false // Non mostrare alcuna legenda per ogni domanda
                        )
                    }
                }
                "Q2b" -> {
                    Text(
                        text = "Elenca fino a 3 altri sintomi o problemi che ti hanno disturbato e indicali:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Mostra la legenda dettagliata una sola volta all'inizio della sezione Q2b
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LegendItem("0", "No per\nniente")
                            LegendItem("1", "Leggermente")
                            LegendItem("2", "Moderatamente")
                            LegendItem("3", "In Modo\nSevero")
                            LegendItem("4", "In modo\nintollerab.")
                        }
                    }
                    
                    section.questions.forEachIndexed { index, question ->
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            OutlinedTextField(
                                enabled = enabled,
                                value = question.questionText,
                                onValueChange = { newText ->
                                    onQuestionResponseChanged(question.questionId, question.score, newText)
                                },
                                label = { Text("Sintomo aggiuntivo ${index + 1}") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true
                            )
                            
                            RadioGroupScale(
                                enabled = enabled,
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
                    var isFirstLegendShown = false
                    var isSecondLegendShown = false
                    
                    section.questions.forEach { question ->
                        // Determina quale domanda stiamo processando
                        val questionNumber = when (question.questionId) {
                            IPOSQuestions.Q3_ANSIA_MALATTIA_TERAPIE_ID -> 3
                            IPOSQuestions.Q4_ANSIA_CARI_ID -> 4
                            IPOSQuestions.Q5_DEPRESSIONE_ID -> 5
                            IPOSQuestions.Q6_PACE_SE_STESSO_ID -> 6
                            IPOSQuestions.Q7_CONDIVIDERE_STATI_ANIMO_ID -> 7
                            IPOSQuestions.Q8_INFO_RICEVUTE_ID -> 8
                            IPOSQuestions.Q9_GESTIONE_PROBLEMI_PRATICI_ID -> 9
                            else -> 0
                        }
                        
                        // Mostra la prima legenda prima di Q3 (Per nulla - Opprimente)
                        if (questionNumber == 3 && !isFirstLegendShown) {
                            isFirstLegendShown = true
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Per nulla",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "0     1     2     3     4",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Opprimente",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        
                        // Mostra la seconda legenda prima di Q6 (scala inversa)
                        if (questionNumber == 6 && !isSecondLegendShown) {
                            isSecondLegendShown = true
                            
                            // Divisore prima del nuovo gruppo Q6-Q8
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    LegendItemInverse("0", "Sempre")
                                    LegendItemInverse("1", "Per la maggior\nparte del tempo")
                                    LegendItemInverse("2", "Qualche\nvolta")
                                    LegendItemInverse("3", "Raramente")
                                    LegendItemInverse("4", "No, per\nniente")
                                }
                            }
                        }
                        
                        // Mostra di nuovo la prima legenda prima di Q9
                        if (questionNumber == 9) {
                            // Divisore prima di Q9
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Per nulla",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "0     1     2     3     4",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Opprimente",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        
                        ScaleQuestionItem(
                            enabled = enabled,
                            questionText = question.questionText,
                            score = question.score,
                            onScoreChange = { newScore ->
                                onQuestionResponseChanged(question.questionId, newScore, question.questionText)
                            },
                            showLegend = false // Non mostrare alcuna legenda per ogni domanda
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
                                enabled = enabled,
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
private fun ScaleQuestionItem(
    enabled: Boolean,
    questionText: String,
    score: Int?,
    onScoreChange: (Int?) -> Unit,
    showDetailedLegend: Boolean = false,
    showLegend: Boolean = true, // Nuovo parametro per controllare se mostrare la legenda
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Mostra la legenda solo se showLegend è true
        if (showLegend) {
            // Aggiungi la legenda sopra i radio button
            if (showDetailedLegend) {
                // Legenda dettagliata per Q2 e Q2b
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem("0", "No per\nniente")
                    LegendItem("1", "Leggermente")
                    LegendItem("2", "Moderatamente")
                    LegendItem("3", "In Modo\nSevero")
                    LegendItem("4", "In modo\nintollerab.")
                }
            } else {
                // Legenda semplice per Q3-Q9
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Per nulla",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Opprimente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        RadioGroupScale(
            enabled = enabled,
            selectedValue = score,
            onValueSelected = { nonNullableScore -> onScoreChange(nonNullableScore) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RowScope.LegendItem(
    number: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 2,
            minLines = 2,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

@Composable
private fun RowScope.LegendItemInverse(
    number: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            maxLines = 2,
            minLines = 2,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}