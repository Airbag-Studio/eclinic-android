package it.airbagstudio.ticare.pages.patientDetails.form.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.form.GetCBITestList
import ch.ticare.eclinic.library.entity.form.GetSeniorSittingTestList
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
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid.getTotalScore
import it.airbagstudio.ticare.utils.DATE_ONLY_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        viewModelScope.launch {
            _uiState.value = HomeUiState.Error(exception.message ?: "Unknown error")
        }
    }

    init {
        loadForms()
    }

    fun loadForms() {
        val caseCod = userDetailRepository.getCurrentCase()?.patientCod ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.value = HomeUiState.Loading

            // It's expected that 'flows' is an Array<List<*>> where each element corresponds to a flow's emission
            @Suppress("UNCHECKED_CAST")
            val cbiList = formRepository.getCbiScaleList(caseCod).results
                ?: listOf<GetCBITestList.CBITestResult>()

            @Suppress("UNCHECKED_CAST")
            val comidList = formRepository.getComidScaleList(caseCod).results ?: listOf()

            @Suppress("UNCHECKED_CAST")
            val iposList = formRepository.getIopsScaleList(caseCod).results ?: listOf()

            @Suppress("UNCHECKED_CAST")
            val seniorSittingList = formRepository.getSeniorSittingScaleList(caseCod).results
                ?: listOf<GetSeniorSittingTestList.SeniorSittingTestResult>()


            val idPallList = formRepository.getIDPallTestScaleList(caseCod).results ?: listOf()
            val camList = formRepository.getCamTestScaleList(caseCod).results ?: listOf()

            val allForms = mutableListOf<DisplayableFormInfo>()

            cbiList.mapTo(allForms) { cbi ->
                DisplayableFormInfo(
                    id = cbi.iD.toString(),
                    formType = FormType.CBI.name,
                    displayName = cbi.nameSurnameUser,
                    creationDate = cbi.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time
                        ?: 0,
                    totalPoints = cbi.getTotalScore()
                )
            }

            idPallList.mapTo(allForms) { cbi ->
                DisplayableFormInfo(
                    id = cbi.iD.toString(),
                    formType = FormType.IDPALL.name,
                    displayName = cbi.nameSurnameUser,
                    creationDate = cbi.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time
                        ?: 0,
                )
            }
            seniorSittingList.mapTo(allForms) { seniorSitting ->
                DisplayableFormInfo(
                    id = seniorSitting.iD.toString(),
                    formType = FormType.SENIOR_SITTING.name,
                    displayName = seniorSitting.nameSurnameUser,
                    creationDate = seniorSitting.evalDateTime.toDate(
                        SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
                    )?.time ?: 0,
                )
            }
            iposList.mapTo(allForms) {
                DisplayableFormInfo(
                    id = it.id.toString(),
                    formType = FormType.IPOS.name,
                    displayName = it.nameSurnameUser,
                    creationDate = it.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time
                        ?: 0,
                )
            }


            comidList.mapTo(allForms) { comid ->
                DisplayableFormInfo(
                    id = comid.iD.toString(),
                    formType = FormType.COMID.name,
                    displayName = comid.nameSurnameUser,
                    creationDate = comid.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time
                        ?: 0,
                    totalPoints = comid.getTotalScore()
                )
            }
            camList.mapTo(allForms) {
                DisplayableFormInfo(
                    id = it.iD.toString(),
                    formType = FormType.CAM.name,
                    displayName = it.nameSurnameUser,
                    creationDate = it.evalDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.time
                        ?: 0)
            }

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

    fun showSaveConfirmationSnackbar(formType: String = "Form") { // Make formType optional or pass it
        viewModelScope.launch {
            _snackbarMessage.emit("$formType salvato con successo.")
        }
    }
}
