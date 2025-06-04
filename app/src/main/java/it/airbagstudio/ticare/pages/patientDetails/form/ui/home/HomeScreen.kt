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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
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
    IPOS3GG("IPOS3gg"), // Added IPOS3GG
    IPOS7GG("IPOS7GG"),  // Added IPOS7GG
    SENIOR_SITTING_ADESIONE("SeniorSittingAdesione"), // Added Senior Sitting Adesione
    SENIOR_SITTING_NON_ADESIONE("SENIOR_SITTING_NON_ADESIONE") // Added Senior Sitting Non Adesione
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
    viewModel: HomeViewModel,
    showSnackbarOnEntry: Boolean,
    onNavigateToForm: (formType: FormType, formId: String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
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
                title = { Text(stringResource(id = R.string.app_name)) },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val currentState = uiState
            when (currentState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                                items(formsInGroup, key = { formInfo -> formInfo.id }) { formInfo ->
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
            headlineContent = { Text("${formInfo.patientName} ${formInfo.patientSurname}") }, // Display name from DisplayableFormInfo
            supportingContent = { Text("Tipo: ${formInfo.formType} - Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(formInfo.lastModified))}") }
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
            title = stringResource(R.string.form_ipos3gg), // Added IPOS3gg
            onClick = { onFormSelected(FormType.IPOS3GG) }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        FormTypeItem(
            title = stringResource(R.string.form_ipos7gg), // Added IPOS7gg
            onClick = { onFormSelected(FormType.IPOS7GG) }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        FormTypeItem(
            title = stringResource(R.string.form_senior_sitting_adesione), // Added Senior Sitting Adesione
            onClick = { onFormSelected(FormType.SENIOR_SITTING_ADESIONE) }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        FormTypeItem(
            title = stringResource(R.string.form_senior_sitting_non_adesione), // Added Senior Sitting Non Adesione
            onClick = { onFormSelected(FormType.SENIOR_SITTING_NON_ADESIONE) }
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview_WithForms() {
    AppTheme {
        val dummyRepo = object : FormRepository {
            override fun getUserDetails(): ch.ticare.eclinic.library.entity.CaseDetail? {
                return null
            }
            override suspend fun saveCbiForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm) {}
            override fun getCbiForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm>> = flowOf(
                listOf(
                    it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm(id = "cbi1", patientData = PatientData(name = "Mario", surname = "Rossi"), formType = "CBI", lastModified = System.currentTimeMillis() - 100000),
                    it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm(id = "cbi2", patientData = PatientData(name = "Luigi", surname = "Verdi"), formType = "CBI", lastModified = System.currentTimeMillis() - 200000)
                )
            )
            override suspend fun getCbiFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm? = null
            override suspend fun deleteCbiForm(formId: String) {}
            override suspend fun saveComidForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm) {}
            override fun getComidForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>> = flowOf(
                listOf(
                    it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm(id = "comid1", patientData = PatientData(name = "Anna", surname = "Bianchi"), formType = "COMID", lastModified = System.currentTimeMillis())
                )
            )
            override suspend fun getComidFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm? = null
            override suspend fun deleteComidForm(formId: String) {}

            // IPOS3gg dummy methods
            override suspend fun saveIPOS3ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm) {}
            override fun getIPOS3ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>> = flowOf(
                listOf(
                    it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm(id = "ipos1", patientData = PatientData(name = "Giovanni", surname = "Gialli"), formType = "IPOS3gg", lastModified = System.currentTimeMillis() - 50000)
                )
            )
            override suspend fun getIPOS3ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm? = null
            override suspend fun deleteIPOS3ggForm(formId: String) {}

            // IPOS7gg dummy methods
            override suspend fun saveIPOS7ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm) {}
            override fun getIPOS7ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>> = flowOf(emptyList()) // Start with empty for preview simplicity
            override suspend fun getIPOS7ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm? = null
            override suspend fun deleteIPOS7ggForm(formId: String) {}

            // SeniorSittingAdesioneForm dummy methods
            override suspend fun saveSeniorSittingAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm) {}
            override fun getSeniorSittingAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>> = flowOf(emptyList())
            override suspend fun getSeniorSittingAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm? = null
            override suspend fun deleteSeniorSittingAdesioneForm(formId: String) {}
            override suspend fun saveSeniorSittingNonAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm) {}
            override fun getSeniorSittingNonAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>> = flowOf(emptyList())
            override suspend fun getSeniorSittingNonAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm? = null
            override suspend fun deleteSeniorSittingNonAdesioneForm(formId: String) {}
        }
        HomeScreen(
            viewModel = HomeViewModel(dummyRepo),
            showSnackbarOnEntry = false,
            onNavigateToForm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview_Empty() {
    AppTheme {
        val dummyRepo = object : FormRepository {
            override fun getUserDetails(): ch.ticare.eclinic.library.entity.CaseDetail? {
                return null
            }
            override suspend fun saveCbiForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm) {}
            override fun getCbiForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm>> = flowOf(emptyList())
            override suspend fun getCbiFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm? = null
            override suspend fun deleteCbiForm(formId: String) {}
            override suspend fun saveComidForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm) {}
            override fun getComidForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm>> = flowOf(emptyList())
            override suspend fun getComidFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm? = null
            override suspend fun deleteComidForm(formId: String) {}

            // IPOS3gg dummy methods for empty preview
            override suspend fun saveIPOS3ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm) {}
            override fun getIPOS3ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm>> = flowOf(emptyList())
            override suspend fun getIPOS3ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm? = null
            override suspend fun deleteIPOS3ggForm(formId: String) {}

            // IPOS7gg dummy methods for empty preview
            override suspend fun saveIPOS7ggForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm) {}
            override fun getIPOS7ggForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm>> = flowOf(emptyList())
            override suspend fun getIPOS7ggFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm? = null
            override suspend fun deleteIPOS7ggForm(formId: String) {}

            // SeniorSittingAdesioneForm dummy methods for empty preview
            override suspend fun saveSeniorSittingAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm) {}
            override fun getSeniorSittingAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm>> = flowOf(emptyList())
            override suspend fun getSeniorSittingAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm? = null
            override suspend fun deleteSeniorSittingAdesioneForm(formId: String) {}
            override suspend fun saveSeniorSittingNonAdesioneForm(form: it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm) {}
            override fun getSeniorSittingNonAdesioneForms(): Flow<List<it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm>> = flowOf(emptyList())
            override suspend fun getSeniorSittingNonAdesioneFormById(formId: String): it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm? = null
            override suspend fun deleteSeniorSittingNonAdesioneForm(formId: String) {}
        }
        HomeScreen(
            viewModel = HomeViewModel(dummyRepo),
            showSnackbarOnEntry = false,
            onNavigateToForm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenericFormListItemPreview() {
    AppTheme {
        GenericFormListItem(
            formInfo = DisplayableFormInfo(
                id = "preview-1",
                formType = "CBI",
                displayName = "Mario Rossi - CBI", // This field is not directly used in GenericFormListItem's text
                creationDate = System.currentTimeMillis(),
                lastModified = System.currentTimeMillis(),
                patientName = "Mario",
                patientSurname = "Rossi"
            ),
            onClick = {}
        )
    }
}
