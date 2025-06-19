package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.components.EmptyState
import it.airbagstudio.ticare.pages.patientDetails.form.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enum per i tipi di form disponibili.
 */
enum class FormType(val typeName: String) {
    CBI("CBI"),
    COMID("COMID"),
    IPOS("IPOS"), // Unified IPOS form replacing IPOS3GG and IPOS7GG
    SENIOR_SITTING("SENIOR_SITTING") // Unified Senior Sitting form replacing ADESIONE and NON_ADESIONE
    // Add other form types here
}

/**
 * Schermata principale dell'app che mostra la lista dei form salvati
 * o uno stato vuoto se non ci sono form.
 *
 * @param viewModel ViewModel per la gestione dello stato.
 * @param onNavigateToForm Callback per navigare alla schermata di un form specifico per la creazione/modifica.
 *                         Accetta il tipo di form e opzionalmente l'ID del form da modificare.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    showSnackbarOnEntry: Boolean,
    onNavigateToForm: (formType: FormType, formId: String?) -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadForms()
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(showSnackbarOnEntry) {
        if (showSnackbarOnEntry) {
            // Assuming formType might be needed for specific messages in future
            val lastSavedFormType = "Form" // Generic for now
            viewModel.showSaveConfirmationSnackbar(lastSavedFormType)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(viewModel.patient?.getCompleteName() ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_form))
                    Text("Registra Scala")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Add Scale header with bigger font
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*
                Icon(
                    painter = painterResource(id = R.drawable.moduli),
                    contentDescription = stringResource(id = R.string.scale_title),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(8.dp))
                */
                Text(
                    text = stringResource(id = R.string.scale_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            val currentState = uiState
            when (currentState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Empty -> {
                    EmptyState(
                        title = stringResource(R.string.empty_state_title),
                        description = stringResource(R.string.empty_state_description)
                    )
                }
                is HomeUiState.Success -> {
                    if (currentState.groupedForms.isEmpty()) {
                         EmptyState(
                            title = stringResource(R.string.empty_state_title),
                            description = stringResource(R.string.empty_state_description)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp) // Increased bottom padding for FAB
                        ) {
                            currentState.groupedForms.forEach { (formTypeString, formsInGroup) ->
                                item {
                                    Text(
                                        text = formTypeString, // Display the group key (e.g., "CBI", "COMID")
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp, horizontal = 8.dp)
                                    )
                                }
                                items(formsInGroup, key = { formInfo -> "${formInfo.id}_${formInfo.creationDate}" }) { formInfo ->
                                    GenericFormListItem(
                                        formInfo = formInfo,
                                        onClick = {
                                            val typeEnum = FormType.values().find { it.typeName == formInfo.formType }
                                            if (typeEnum != null) {
                                                onNavigateToForm(typeEnum, formInfo.id)
                                            } else {
                                                // Handle unknown form type, maybe show a toast or log
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadForms() }) {
                            Text("Riprova")
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                FormSelectorBottomSheet(onFormSelected = { selectedFormType ->
                    coroutineScope.launch {
                        launch { bottomSheetState.hide() }
                        showBottomSheet = false
                        onNavigateToForm(selectedFormType, null) // For new form, ID is null
                    }
                })
            }
        }
    }
}

@Composable
fun GenericFormListItem(formInfo: DisplayableFormInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            headlineContent = { Text("Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(formInfo.creationDate))}") },
           supportingContent = { if (formInfo.totalPoints != null) Text("Punteggio: ${formInfo.totalPoints}") } // Da aggiornare con il punteggio reale se disponibile
        )
    }
}

@Composable
fun FormSelectorBottomSheet(onFormSelected: (FormType) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.select_form_type),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FormTypeItem(
            title = stringResource(R.string.form_cbi), // Use string resource for CBI
            onClick = { onFormSelected(FormType.CBI) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        FormTypeItem(
            title = stringResource(R.string.form_comid), // Use string resource for COMID
            onClick = { onFormSelected(FormType.COMID) }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        FormTypeItem(
            title = "IPOS", // Unified IPOS form
            onClick = { onFormSelected(FormType.IPOS) }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        FormTypeItem(
            title = "Senior Sitting", // Unified Senior Sitting form
            onClick = { onFormSelected(FormType.SENIOR_SITTING) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FormTypeItem(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}