package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCarePlannedActivity
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectCareActivityPopupUIState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val query: String,
    val unplannedActivities: List<ActivityListItem>,
    val plannedActivities: List<ActivityListItem>


){
    data class ActivityListItem(
        val title:String,
        val id: Int,
        val isPlanned: Boolean
    )
}

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow<String>("")
    private val carePlanId = MutableStateFlow<Int?>(null)
    private val patientCode = MutableStateFlow<String?>(null)
    private var notPlannedActivities = MutableStateFlow<List<HomeCareUnplannedActivity>>(listOf())

    private val plannedActivities = combine(patientCode, carePlanId) { patientCode, carePlanId ->
        if (patientCode != null && carePlanId != null) {
            homeCareActivitiesRepository.getHomeCareActivitiesPlanned(
                patientCode,
                carePlanId
            ).results
        } else {
            null
        }
    }

    val uiState = combine(
        isLoading,
        errorMessage,
        query,
        notPlannedActivities,
        plannedActivities
    ) { isLoading, errorMessage, query, notPlannedActivities, plannedActivities ->
        val unPlannedItems = if (query.isNotEmpty()) {
            notPlannedActivities.filter { it.desc.contains(query, true) }
        } else {
            notPlannedActivities
        }.map { SelectCareActivityPopupUIState.ActivityListItem(it.desc, it.id, false) }

        val plannedItems = plannedActivities?.map { SelectCareActivityPopupUIState.ActivityListItem(it.type, it.id, false) } ?: listOf()

        SelectCareActivityPopupUIState(
            isLoading = isLoading,
            errorMessage = errorMessage,
            query = query,
            unplannedActivities = unPlannedItems,
            plannedActivities = plannedItems
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = SelectCareActivityPopupUIState(
            isLoading = true,
            errorMessage = null,
            query = "",
            unplannedActivities = listOf(),
            plannedActivities = listOf()
        )
    )

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            errorMessage.value = throwable.localizedMessage
            isLoading.value = false
        }

    init {
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = homeCareActivitiesRepository.getHomeCareActivitiesUnplanned()
            notPlannedActivities.value = res.results ?: listOf()
            errorMessage.value = res.error?.desc
            isLoading.value = false
        }
    }


    fun setCarePlanId(id: Int) {
        carePlanId.value = id
    }

    fun setPatientCode(code: String) {
        patientCode.value = code
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun clearErrors() {
        errorMessage.value = null
    }
}