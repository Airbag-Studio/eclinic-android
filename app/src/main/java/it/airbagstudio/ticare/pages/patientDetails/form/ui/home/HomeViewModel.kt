package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.GetCBITestList
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBIForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.getTotalScore
import it.airbagstudio.ticare.utils.DATE_ONLY_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.toDate
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
import javax.inject.Inject

/**
 * ViewModel per la gestione dello stato della home page.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    //private val oldFormRepository: OldFormRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    
    // Expose patient information for the title
    val patient: CaseDetail? get() = userDetailRepository.getCurrentCase()
    
    init {
        loadForms()
    }
    
    fun loadForms() {
        val caseCod = userDetailRepository.getCurrentCase()?.patientCod ?: return
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            //val cbiFormsFlow = formRepository.getCbiScaleList(caseCod)
            //val comidFormsFlow = oldFormRepository.getComidForms()
            //val ipos3ggFormsFlow = oldFormRepository.getIPOS3ggForms() // Added IPOS3gg flow
            //val ipos7ggFormsFlow = oldFormRepository.getIPOS7ggForms() // Added IPOS7gg flow
            //val seniorSittingAdesioneFormsFlow = oldFormRepository.getSeniorSittingAdesioneForms() // Added SeniorSittingAdesione flow
            //val seniorSittingNonAdesioneFormsFlow = oldFormRepository.getSeniorSittingNonAdesioneForms() // Added SeniorSittingNonAdesione flow

            // Combine flows of CBIForm, COMIDForm, IPOS3ggForm, IPOS7ggForm, SeniorSittingAdesioneForm, and SeniorSittingNonAdesioneForm

                // It's expected that 'flows' is an Array<List<*>> where each element corresponds to a flow's emission
                @Suppress("UNCHECKED_CAST")
                val cbiList = formRepository.getCbiScaleList(caseCod).results ?: listOf<GetCBITestList.CBITestResult>()
                @Suppress("UNCHECKED_CAST")
                val comidList = listOf<COMIDForm>()
                @Suppress("UNCHECKED_CAST")
                val ipos3ggList = listOf<IPOS3ggForm>()
                @Suppress("UNCHECKED_CAST")
                val ipos7ggList = listOf<IPOS7ggForm>()
                @Suppress("UNCHECKED_CAST")
                val seniorSittingList = listOf<SeniorSittingAdesioneForm>()
                @Suppress("UNCHECKED_CAST")
                val seniorSittingNonAdesioneList = listOf<SeniorSittingNonAdesioneForm>()

                val allForms = mutableListOf<DisplayableFormInfo>()

                cbiList.mapTo(allForms) { cbi ->
                    DisplayableFormInfo(
                        id = cbi.iD.toString(),
                        formType = FormType.CBI.name,
                        displayName = cbi.nameSurnameUser,
                        creationDate = cbi.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time ?: 0,
                        totalPoints = cbi.getTotalScore()
                    )
                }
            /*
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
                *
             */
                allForms.sortedByDescending { it.creationDate } // Return the sorted list


                if (allForms.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    val groupedAndSortedForms = allForms
                        .groupBy { it.formType }
                        .mapValues { entry -> entry.value.sortedByDescending { it.creationDate } } // Sort within groups
                        .toSortedMap() // Sort groups by formType
                    _uiState.value = HomeUiState.Success(groupedAndSortedForms)
                }

        }
    }
/*
    fun deleteForm(formId: String, formType: String) {
        viewModelScope.launch {
            try {
                when (formType) {
                    "CBI" -> oldFormRepository.deleteCbiForm(formId)
                    "COMID" -> oldFormRepository.deleteComidForm(formId)
                    "IPOS3gg" -> oldFormRepository.deleteIPOS3ggForm(formId) // Added IPOS3gg case
                    "IPOS7GG" -> oldFormRepository.deleteIPOS7ggForm(formId) // Added IPOS7gg case
                    "SeniorSittingAdesione" -> oldFormRepository.deleteSeniorSittingAdesioneForm(formId) // Added SeniorSittingAdesione case
                    "SENIOR_SITTING_NON_ADESIONE" -> oldFormRepository.deleteSeniorSittingNonAdesioneForm(formId) // Added SeniorSittingNonAdesione case
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
*/
    fun showSaveConfirmationSnackbar(formType: String = "Form") { // Make formType optional or pass it
        viewModelScope.launch {
            _snackbarMessage.emit("$formType salvato con successo.")
        }
    }
}
