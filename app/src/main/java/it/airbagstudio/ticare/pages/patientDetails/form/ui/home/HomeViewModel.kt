package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel per la gestione dello stato della home page.
 */
// TODO: Inject FormRepository using Hilt or Koin
class HomeViewModel(
    private val formRepository: FormRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    
    init {
        loadForms()
    }
    
    fun loadForms() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val cbiFormsFlow = formRepository.getCbiForms()
            val comidFormsFlow = formRepository.getComidForms()
            val ipos3ggFormsFlow = formRepository.getIPOS3ggForms() // Added IPOS3gg flow
            val ipos7ggFormsFlow = formRepository.getIPOS7ggForms() // Added IPOS7gg flow
            val seniorSittingAdesioneFormsFlow = formRepository.getSeniorSittingAdesioneForms() // Added SeniorSittingAdesione flow
            val seniorSittingNonAdesioneFormsFlow = formRepository.getSeniorSittingNonAdesioneForms() // Added SeniorSittingNonAdesione flow

            // Combine flows of CBIForm, COMIDForm, IPOS3ggForm, IPOS7ggForm, SeniorSittingAdesioneForm, and SeniorSittingNonAdesioneForm
            combine(
                cbiFormsFlow,
                comidFormsFlow,
                ipos3ggFormsFlow,
                ipos7ggFormsFlow,
                seniorSittingAdesioneFormsFlow,
                seniorSittingNonAdesioneFormsFlow
            ) { flows ->
                // It's expected that 'flows' is an Array<List<*>> where each element corresponds to a flow's emission
                @Suppress("UNCHECKED_CAST")
                val cbiList = flows[0] as List<CBIForm>
                @Suppress("UNCHECKED_CAST")
                val comidList = flows[1] as List<COMIDForm>
                @Suppress("UNCHECKED_CAST")
                val ipos3ggList = flows[2] as List<IPOS3ggForm>
                @Suppress("UNCHECKED_CAST")
                val ipos7ggList = flows[3] as List<IPOS7ggForm>
                @Suppress("UNCHECKED_CAST")
                val seniorSittingList = flows[4] as List<SeniorSittingAdesioneForm>
                @Suppress("UNCHECKED_CAST")
                val seniorSittingNonAdesioneList = flows[5] as List<SeniorSittingNonAdesioneForm>

                val allForms = mutableListOf<DisplayableFormInfo>()

                cbiList.mapTo(allForms) { cbi ->
                    DisplayableFormInfo(
                        id = cbi.id,
                        formType = cbi.formType,
                        displayName = "${cbi.patientData.name} ${cbi.patientData.surname}",
                        creationDate = cbi.creationDate,
                        lastModified = cbi.lastModified,
                        patientName = cbi.patientData.name,
                        patientSurname = cbi.patientData.surname
                    )
                }
                comidList.mapTo(allForms) { comid ->
                    DisplayableFormInfo(
                        id = comid.id,
                        formType = comid.formType,
                        displayName = "${comid.patientData.name} ${comid.patientData.surname}",
                        creationDate = comid.creationDate,
                        lastModified = comid.lastModified,
                        patientName = comid.patientData.name,
                        patientSurname = comid.patientData.surname
                    )
                }
                ipos3ggList.mapTo(allForms) { ipos3gg ->
                    DisplayableFormInfo(
                        id = ipos3gg.id,
                        formType = ipos3gg.formType,
                        displayName = "${ipos3gg.patientData.name} ${ipos3gg.patientData.surname}",
                        creationDate = ipos3gg.creationDate,
                        lastModified = ipos3gg.lastModified,
                        patientName = ipos3gg.patientData.name,
                        patientSurname = ipos3gg.patientData.surname
                    )
                }
                ipos7ggList.mapTo(allForms) { ipos7gg ->
                    DisplayableFormInfo(
                        id = ipos7gg.id,
                        formType = ipos7gg.formType,
                        displayName = "${ipos7gg.patientData.name} ${ipos7gg.patientData.surname}",
                        creationDate = ipos7gg.creationDate,
                        lastModified = ipos7gg.lastModified,
                        patientName = ipos7gg.patientData.name,
                        patientSurname = ipos7gg.patientData.surname
                    )
                }
                seniorSittingList.mapTo(allForms) { seniorSitting ->
                    DisplayableFormInfo(
                        id = seniorSitting.id,
                        formType = seniorSitting.formType,
                        displayName = "${seniorSitting.patientData.name} ${seniorSitting.patientData.surname}",
                        creationDate = seniorSitting.creationDate,
                        lastModified = seniorSitting.lastModified,
                        patientName = seniorSitting.patientData.name,
                        patientSurname = seniorSitting.patientData.surname
                    )
                }
                seniorSittingNonAdesioneList.mapTo(allForms) { seniorSittingNonAdesione ->
                    DisplayableFormInfo(
                        id = seniorSittingNonAdesione.id,
                        formType = seniorSittingNonAdesione.formType,
                        displayName = "${seniorSittingNonAdesione.patientData.name} ${seniorSittingNonAdesione.patientData.surname}",
                        creationDate = seniorSittingNonAdesione.creationDate,
                        lastModified = seniorSittingNonAdesione.lastModified,
                        patientName = seniorSittingNonAdesione.patientData.name,
                        patientSurname = seniorSittingNonAdesione.patientData.surname
                    )
                }
                allForms.sortedByDescending { it.lastModified } // Return the sorted list
            }
            .catch { e ->
                _uiState.value = HomeUiState.Error("Errore nel caricamento dei form: ${e.message}")
                _snackbarMessage.emit("Errore nel caricamento dei form: ${e.message}")
            }
            .collectLatest { combinedDisplayableForms ->
                if (combinedDisplayableForms.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    val groupedAndSortedForms = combinedDisplayableForms
                        .groupBy { it.formType }
                        .mapValues { entry -> entry.value.sortedByDescending { it.lastModified } } // Sort within groups
                        .toSortedMap() // Sort groups by formType
                    _uiState.value = HomeUiState.Success(groupedAndSortedForms)
                }
            }
        }
    }

    fun deleteForm(formId: String, formType: String) {
        viewModelScope.launch {
            try {
                when (formType) {
                    "CBI" -> formRepository.deleteCbiForm(formId)
                    "COMID" -> formRepository.deleteComidForm(formId)
                    "IPOS3gg" -> formRepository.deleteIPOS3ggForm(formId) // Added IPOS3gg case
                    "IPOS7GG" -> formRepository.deleteIPOS7ggForm(formId) // Added IPOS7gg case
                    "SeniorSittingAdesione" -> formRepository.deleteSeniorSittingAdesioneForm(formId) // Added SeniorSittingAdesione case
                    "SENIOR_SITTING_NON_ADESIONE" -> formRepository.deleteSeniorSittingNonAdesioneForm(formId) // Added SeniorSittingNonAdesione case
                    // Add cases for other form types as they are implemented
                    else -> {
                        _snackbarMessage.emit("Tipo di form non supportato per l'eliminazione.")
                        return@launch
                    }
                }
                // The combined Flow should update the list automatically.
                _snackbarMessage.emit("Form $formType eliminato con successo.")
            } catch (e: IOException) {
                _snackbarMessage.emit("Errore durante l'eliminazione del form $formType: ${e.message}")
            } catch (e: Exception) {
                _snackbarMessage.emit("Errore imprevisto durante l'eliminazione del form $formType: ${e.message}")
            }
        }
    }

    fun showSaveConfirmationSnackbar(formType: String = "Form") { // Make formType optional or pass it
        viewModelScope.launch {
            _snackbarMessage.emit("$formType salvato con successo.")
        }
    }
}
