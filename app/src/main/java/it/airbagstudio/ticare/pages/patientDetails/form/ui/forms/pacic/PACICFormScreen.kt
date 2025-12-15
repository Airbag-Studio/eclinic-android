package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.pacic

import android.R.attr.fontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import it.airbagstudio.ticare.pages.carePlans.create.CreateEditCareScreenUiState
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.PACICSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.SectionHeader
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.LegendItem
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.PatientDataSection
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting.ScaleQuestionItem
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting.SeniorSittingFormScreen
import kotlin.text.Typography.section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PACICFormScreen (
    formId: String?,
    onClose: () -> Unit,
    onSaved: (formId: String) -> Unit,
    viewModel: PACICFormViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isEditingEnabled by remember { mutableStateOf(false) }
    var isFormEnabled = isEditingEnabled || formId == null
    LaunchedEffect(formId) {
        viewModel.initForm(formId)
    }

    LaunchedEffect(uiState) {
        if (uiState is PACICFormUiState.Saved) {
            onSaved((uiState as PACICFormUiState.Saved).savedForm.id)
        }
    }

    // Show validation errors
    LaunchedEffect(uiState) {
        val state = uiState as? PACICFormUiState.Editing ?: return@LaunchedEffect
        if (state.validationErrors.isNotEmpty()) {
            snackbarHostState.showSnackbar(message = state.validationErrors.first())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pacic-S")
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
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
        }
    ) {
        paddingValues ->
        when (val currentState = uiState) {
            is PACICFormUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PACICFormUiState.Error ->{
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
            is PACICFormUiState.Editing -> {
                PacicFormContent(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    enabled = isFormEnabled,
                    uiState = currentState,
                    formId = formId,
                    viewModel = viewModel
                )
            }
            is PACICFormUiState.Saved -> {
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

@Composable
private fun PacicFormContent(
    enabled: Boolean,
    uiState: PACICFormUiState.Editing,
    formId: String?,
    modifier: Modifier = Modifier,
    viewModel: PACICFormViewModel
){
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Sezione dati paziente
        item {
            PatientDataSection(
                patientData = uiState.patientData,
                compilationTimestamp = uiState.form.compilationTimestamp,
                onCompilationDateTimeSelected = { viewModel.onCompilationDateTimeSelected(it) },
            )
        }
        // Legenda delle domande PACICS
        item {
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
                    LegendItem("1", "Mai")
                    LegendItem("2", "Raramente")
                    LegendItem("3", "A volte")
                    LegendItem("4", "Spesso")
                    LegendItem("5", "Sempre")
                }
            }
        }

        // Domande del questionario
        item{
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionHeader(title = PACICSQuestions.introText, showScore = false)
                    uiState.form.questions.forEach { question ->
                        ScaleQuestionItem(
                            enabled = enabled,
                            questionText = question.questionText,
                            score = question.score,
                            onScoreChange = { newScore ->
                                viewModel.onQuestionResponseChanged(
                                    question.questionId,
                                    newScore ?: 0
                                )
                            }
                        )
                    }
                }
            }
        }
        item{
            Button(
                onClick = { viewModel.saveForm(formId) },
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