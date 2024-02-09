package it.airbagstudio.ticare.pages.carePlans.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCarePlan
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CarePlanesListScreenUIState(
    val isLoading: Boolean,
    val patientName:String,
    val errorMessage: String?,
    val items: List<CarePlanesListItem>
)

@HiltViewModel
class CarePlanesListScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val patientName = userDetailRepository.getCurrentCase()?.getCompleteName() ?: ""
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val plans = MutableStateFlow<List<HomeCarePlan>>(listOf())
    private val modifiedIds = MutableStateFlow<List<String>>(listOf())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading.value = false
        errorMessage.value = throwable.localizedMessage

    }

    var uiState = combine(isLoading, errorMessage,plans,modifiedIds) { isLoading, errorMessage,plans,modifiedIds ->
        val items = plans.map { CarePlanesListItem(
            title = it.title,
            date = it.openDate.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: "",
            id = it.id,
            hasDataToUpload = modifiedIds.contains(it.id.toString())
        ) }
        CarePlanesListScreenUIState(
            isLoading = false,
            patientName = patientName,
            errorMessage = null,
            items = items
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CarePlanesListScreenUIState(
            isLoading = true,
            patientName = patientName,
            errorMessage = null,
            items = listOf()
        )
    )

    init {
        downloadData()
    }

    fun downloadData(){
        clearError()
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            modifiedIds.value = offlineOnlineRepository.getModifiedIdForSection(patientCod,OfflineSection.HomeCarePlans)
            val res = homeCareActivitiesRepository.getHomeCarePlans(patientCod)
            if(res.status == "success"){
                plans.value = res.results ?: listOf()
            }else if (res.error != null){
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false
        }
    }

    fun clearError(){
        errorMessage.value = null
    }
}