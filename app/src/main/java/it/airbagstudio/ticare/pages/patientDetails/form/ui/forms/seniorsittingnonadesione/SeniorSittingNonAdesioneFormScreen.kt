package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingnonadesione

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.SectionHeader
import it.airbagstudio.ticare.pages.patientDetails.form.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.*
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeniorSittingNonAdesioneFormScreen(
    formId: String?, // Null for new form
    viewModel: SeniorSittingNonAdesioneFormViewModel,
    onClose: () -> Unit,
    onSaved: (formId: String, formType: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        if (uiState is SeniorSittingNonAdesioneFormUiState.Saved) {
            val savedState = uiState as SeniorSittingNonAdesioneFormUiState.Saved
            onSaved(savedState.savedForm.id, savedState.savedForm.formType)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.form_senior_sitting_non_adesione_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SeniorSittingNonAdesioneFormUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SeniorSittingNonAdesioneFormUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is SeniorSittingNonAdesioneFormUiState.Saved -> {
                // The LaunchedEffect handles navigation, so this can be empty or show a success message.
                // For now, keeping it simple as navigation will occur.
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Form salvato con successo!") // Placeholder, navigation will occur
                }
            }
            is SeniorSittingNonAdesioneFormUiState.Editing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    // Patient Data Section (Read-only except for compilation timestamp)
                    PatientDataSection(
                        patientData = state.patientData,
                        compilationTimestamp = state.compilationTimestamp,
                        onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section 1: Motivi della non adesione (Checkboxes)
                    val section1 = state.sections.find { it.order == 1 }
                    if (section1 != null) {
                        FormSectionCard {
                            SectionHeader(title = stringResource(R.string.senior_sitting_non_adesione_section1_title))
                            Text(
                                text = stringResource(R.string.senior_sitting_non_adesione_intro_section1),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            section1.questions.forEach { question ->
                                CheckboxQuestionItem(
                                    questionText = question.questionText,
                                    isChecked = question.score == 1,
                                    onCheckedChange = { isChecked ->
                                        viewModel.onCheckboxChanged(section1.order, question.questionId, isChecked)
                                    }
                                )
                            }
                            if (!state.isSection1Valid) {
                                Text(
                                    stringResource(R.string.error_non_adesione_section1_required),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Section 2: Suggerimenti, osservazioni (Open Question)
                    val section2 = state.sections.find { it.order == 2 }
                    if (section2 != null) {
                        val openQuestion = section2.questions.first() // Assuming only one question
                        FormSectionCard {
                            SectionHeader(title = stringResource(R.string.senior_sitting_non_adesione_section2_title))
                            OutlinedTextField(
                                value = openQuestion.questionText,
                                onValueChange = { text ->
                                    viewModel.onOpenQuestionChanged(section2.order, openQuestion.questionId, text)
                                },
                                label = { Text(stringResource(R.string.senior_sitting_non_adesione_suggerimenti_label)) },
                                placeholder = { Text(stringResource(id = R.string.placeholder_scrivi_qui)) },
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                                isError = !state.isSection2Valid,
                                singleLine = false
                            )
                            if (!state.isSection2Valid) {
                                Text(
                                    stringResource(R.string.error_non_adesione_section2_required),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    if (state.generalError != null) {
                        Text(
                            text = state.generalError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.saveForm() },
                        enabled = state.canSave,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isSaving) {
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
fun PatientDataSection(
    patientData: PatientData,
    compilationTimestamp: Long,
    onCompilationDateTimeSelected: (Long) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    FormSectionCard {
        SectionHeader(title = stringResource(id = R.string.patient_data_section_title))
        ReadOnlyTextField(label = stringResource(id = R.string.patient_name_label_static), value = patientData.name)
        ReadOnlyTextField(label = stringResource(id = R.string.patient_surname_label_static), value = patientData.surname)
        ReadOnlyTextField(
            label = stringResource(id = R.string.patient_birth_date_label_static),
            value = patientData.birthDate?.let { dateFormat.format(Date(it)) } ?: stringResource(id = R.string.select_date_hint)
        )
        ReadOnlyTextField(label = stringResource(id = R.string.label_zone), value = patientData.zone)

        DateTimePickerInputField(
            label = stringResource(id = R.string.compilation_date_time_label),
            selectedTimestamp = compilationTimestamp,
            onTimestampSelected = onCompilationDateTimeSelected
            // No error state needed for compilation timestamp as it defaults to current time
        )
    }
}

@Composable
fun ReadOnlyTextField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.05f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        enabled = false // To get the disabled look and feel for read-only
    )
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


@Composable
fun FormSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
       // colors = CardDefaults.cardColors(containerColor = MaterialTheme.formColors.sectionBackground) // Using formColors
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeniorSittingNonAdesioneFormScreenNew() {
    AppTheme {
        val mockViewModel = SeniorSittingNonAdesioneFormViewModel(
            formId = null,
            formRepository = object : FormRepository {
                override suspend fun saveCbiForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm) {}
                override fun getCbiForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getCbiFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm? = null
                override suspend fun deleteCbiForm(formId: String) {}
                override suspend fun saveComidForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm) {}
                override fun getComidForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getComidFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm? = null
                override suspend fun deleteComidForm(formId: String) {}
                override suspend fun saveIPOS3ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm) {}
                override fun getIPOS3ggForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getIPOS3ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm? = null
                override suspend fun deleteIPOS3ggForm(formId: String) {}
                override suspend fun saveIPOS7ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm) {}
                override fun getIPOS7ggForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getIPOS7ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm? = null
                override suspend fun deleteIPOS7ggForm(formId: String) {}
                override suspend fun saveSeniorSittingAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm) {}
                override fun getSeniorSittingAdesioneForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getSeniorSittingAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm? = null
                override suspend fun deleteSeniorSittingAdesioneForm(formId: String) {}
                override suspend fun saveSeniorSittingNonAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm) {}
                override fun getSeniorSittingNonAdesioneForms(): kotlinx.coroutines.flow.Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun getSeniorSittingNonAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm? = null
                override suspend fun deleteSeniorSittingNonAdesioneForm(formId: String) {}
            },
            patientData = PatientData(name = "Mario", surname = "Rossi", birthDate = System.currentTimeMillis() - 30L * 365 * 24 * 60 * 60 * 1000, zone = "Centro")
        )
        SeniorSittingNonAdesioneFormScreen(
            formId = null,
            viewModel = mockViewModel,
            onClose = {},
            onSaved = { _, _ -> }
        )
    }
}
