package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DatePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.SectionHeader
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.CAMQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.PatientDataSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CAMFormScreen(
    formId: String?,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CAMFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditingEnabled by remember { mutableStateOf(false) }
    var isFormEnabled = isEditingEnabled || formId == null

    LaunchedEffect(formId) {
        viewModel.loadForm(formId?.toInt())
    }

    when (val state = uiState) {
        is CAMFormUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CAMFormUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.message}")
            }
        }
        is CAMFormUiState.Saved -> {
            LaunchedEffect(Unit) {
                onSaved()
            }
        }
        is CAMFormUiState.Editing -> {
            val editingState = state
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.form_cam_title)) },
                        navigationIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
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
                        },
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Patient Data Section
                    PatientDataSection(
                        patientData = editingState.form.patientData,
                        compilationTimestamp = editingState.form.compilationTimestamp,
                        onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Introductory Text
                    Text(
                        text = CAMQuestions.introductoryText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Form Sections (Domains)
                    editingState.form.sections.forEach { section ->
                        SectionHeader(title = section.title, showScore = false) // Score not shown for CAM
                        Spacer(modifier = Modifier.height(8.dp))
                        section.questions.forEach { question ->
                            YesNoQuestionItem(
                                enabled = isFormEnabled,
                                question = question,
                                onResponseSelected = { responseScore ->
                                    viewModel.onQuestionResponseChanged(section.order, question.questionId, responseScore)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.saveForm(formId?.toInt()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !editingState.isSaving && isFormEnabled && editingState.isFormValid,
                    ) {
                        if (editingState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(id = R.string.save_and_submit))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun YesNoQuestionItem(
    enabled: Boolean,
    question: QuestionResponse,
    onResponseSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val yesSelected = question.score == 1
            val noSelected = question.score == 0

            RadioButton(
                enabled = enabled,
                selected = yesSelected,
                onClick = { onResponseSelected(1) }
            )
            Text(
                text = stringResource(id = R.string.yes),
                modifier = Modifier.padding(start = 4.dp, end = 16.dp)
            )

            RadioButton(
                enabled = enabled,
                selected = noSelected,
                onClick = { onResponseSelected(0) }
            )
            Text(
                text = stringResource(id = R.string.no),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
