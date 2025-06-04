package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi

// import androidx.compose.material.icons.filled.Check // Removed Check icon
// import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CaregiverData // No longer directly passed to CaregiverDataSection like this
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.CaregiverInfoCard
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.CaregiverSelectionModalSheet
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.CaregiverSelectorButton
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.DateTimePickerInputField
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.FormSection
import it.airbagstudio.ticare.ui.theme.formColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Schermata di compilazione del form CBI.
 *
 * @param formId ID del form da caricare (null per nuovo form)
 * @param onClose Callback per la chiusura della schermata
 * @param onSaved Callback per il salvataggio completato
 * @param viewModel ViewModel per la gestione dello stato
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbiFormScreen(
    formId: String? = null,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CbiFormViewModel = viewModel()
) {
    // Inizializza il form
    LaunchedEffect(formId) {
        viewModel.initForm(formId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Gestione dello stato salvato
    LaunchedEffect(uiState) {
        if (uiState is CbiFormUiState.Saved) {
            onSaved()
        }
    }
    
    // Mostra errori di validazione
    LaunchedEffect(uiState) {
        val state = uiState as? CbiFormUiState.Editing ?: return@LaunchedEffect
        if (state.validationErrors.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = state.validationErrors.first()
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cbi_form_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Torna indietro"
                        )
                    }
                },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (uiState) {
            is CbiFormUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is CbiFormUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = (uiState as CbiFormUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is CbiFormUiState.Editing -> {
                val editingState = uiState as CbiFormUiState.Editing
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // New introductory text block
                    Column {
                        Text(
                            text = "CAREGIVER BURDEN INVENTORY (CBI)",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = """
                            (Novak M. e Guest C., Gerontologist, 29, 798-803, 1989)

                            Le domande si rivolgono a chi assiste un congiunto con disagio cognitivo; vi invitiamo a rispondere indicando il punteggio che più rispecchia il vostro vissuto o la vostra personale impressione.
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp)) // Spacer after the text block, before the patient data section
                    }
                    // End of new introductory text block

                    // Sezione dati paziente
                    PatientDataSection(
                        patientData = editingState.patientData,
                        compilationTimestamp = editingState.compilationTimestamp,
                        onCompilationDateTimeSelected = viewModel::onCompilationDateTimeSelected,
                        isPatientDataSectionInvalid = editingState.invalidFieldKeys.contains(CbiFormViewModel.ValidationKeys.PATIENT_DATA_SECTION),
                        compilationTimestampError = editingState.compilationTimestampError
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Updated Caregiver Section
                    val selectedCaregiver = editingState.selectedCaregiver
                    if (selectedCaregiver == null) {
                        CaregiverSelectorButton(onClick = { viewModel.onCaregiverSelectorClick() })
                    } else {
                        CaregiverInfoCard(caregiver = selectedCaregiver)
                        Spacer(modifier = Modifier.height(8.dp))
                        CaregiverSelectorButton(onClick = { viewModel.onCaregiverSelectorClick() })
                    }

                    if (editingState.isCaregiverSelectionModalVisible) {
                        CaregiverSelectionModalSheet(
                            uiState = editingState,
                            onDismiss = { viewModel.onDismissCaregiverSelectionModal() },
                            onCaregiverSelected = { caregiver -> viewModel.onCaregiverSelected(caregiver) },
                            onAddNewCaregiverClick = { viewModel.onAddNewCaregiverClick() },
                            onSaveNewCaregiver = { viewModel.onSaveNewCaregiver() },
                            onCancelAddCaregiver = { viewModel.onCancelAddCaregiver() },
                            onNewCaregiverFirstNameChanged = { viewModel.onNewCaregiverFirstNameChanged(it) },
                            onNewCaregiverLastNameChanged = { viewModel.onNewCaregiverLastNameChanged(it) },
                            onNewCaregiverRelationshipChanged = { viewModel.onNewCaregiverRelationshipChanged(it) },
                            onNewCaregiverContactChanged = { viewModel.onNewCaregiverContactChanged(it) }
                        )
                    }
                    // End of Updated Caregiver Section
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Le 5 sezioni del carico
                    editingState.sections.forEach { section ->
                        FormSection(
                            sectionType = section.type,
                            questions = section.questions,
                            onScoreChanged = { questionId, score ->
                                viewModel.updateQuestionScore(section.type, questionId, score)
                            },
                            partialScore = section.partialScore,
                            isInvalid = editingState.invalidFieldKeys.contains(CbiFormViewModel.ValidationKeys.sectionKey(section.type)),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Riepilogo punteggi
                    // ScoreSummarySection(
                    //     sections = editingState.sections,
                    //     totalScore = editingState.totalScore
                    // )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.saveForm() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !editingState.isSaving // Disable button when saving
                    ) {
                        if (editingState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp), // Match text height
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Salva e Invia")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            is CbiFormUiState.Saved -> {
                // Gestito tramite LaunchedEffect
            }
        }
    }
}

/**
 * Sezione per i dati del paziente.
 *
 * @param patientData Dati del paziente
 * @param onPatientDataChanged Callback per la modifica dei dati
 * @param isInvalid Flag per indicare se la sezione contiene campi invalidi
 */
@Composable
fun PatientDataSection(
    patientData: PatientData,
    compilationTimestamp: Long,
    onCompilationDateTimeSelected: (Long) -> Unit,
    isPatientDataSectionInvalid: Boolean = false, // General invalid flag for the section if needed
    compilationTimestampError: String? = null
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateTimeFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.formColors.sectionBackground
        ),
        border = if (isPatientDataSectionInvalid) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.patient_data),
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Static display for Patient Name and Surname
            Text(
                text = "${stringResource(R.string.patient_name_label_static)}: ${patientData.name}", // Uses updated shorter label
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${stringResource(R.string.patient_surname_label_static)}: ${patientData.surname}", // Uses updated shorter label
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Static display for Patient Birth Date
            val birthDateString = patientData.birthDate?.let { dateFormatter.format(Date(it)) } ?: "N/D"
            Text(
                text = "${stringResource(R.string.patient_birth_date_label_static)}: $birthDateString",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp) // More space before new fields
            )

            // New Combined DateTimePickerInputField
            DateTimePickerInputField(
                label = stringResource(R.string.compilation_date_time_label),
                selectedTimestamp = compilationTimestamp,
                onTimestampSelected = onCompilationDateTimeSelected,
                error = compilationTimestampError,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Sezione per i dati del caregiver.
 *
 * @param caregiverData Dati del caregiver
 * @param onCaregiverDataChanged Callback per la modifica dei dati (REMOVED as direct editing is replaced by modal)
 * @param isInvalid Flag per indicare se la sezione contiene campi invalidi (REMOVED - handled by selectedCaregiver state)
 */
// Composable fun CaregiverDataSection(
//     caregiverData: CaregiverData, // This parameter might change or be derived from selectedCaregiver
//     onCaregiverDataChanged: (CaregiverData) -> Unit, // This callback might be removed or changed
//     isInvalid: Boolean = false
// ) {
    // The content of this section is now replaced by CaregiverSelectorButton and CaregiverInfoCard,
    // and the modal flow.
    // If there's a need for a visual section header "Dati del caregiver" even with the new flow,
    // it can be added here, but the input fields are gone.

    // Example: Keeping the header if desired
    // Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
    //     Text(
    //         text = stringResource(R.string.caregiver_data),
    //         style = MaterialTheme.typography.titleLarge,
    //         modifier = Modifier.padding(bottom = 8.dp) // Add padding if Card is removed
    //     )
    //     // The CaregiverSelectorButton and CaregiverInfoCard will be placed here by the parent
    // }
// }

/**
 * Estensione per gestire i Boolean nullable.
 */
fun Boolean?.isTrue(): Boolean = this == true
