package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCareActivity
import ch.ticare.eclinic.library.entity.HomeCareActivitySave
import ch.ticare.eclinic.library.entity.HomeCarePlannedActivity
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
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
        val isPlanned: Boolean,
        val isTransferActivity: Boolean
    )
}

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
) : ViewModel() {

    private val transferActivityCode = homeCareActivitiesRepository.getTransferActivityCode()
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow<String>("")
    private val carePlanId = MutableStateFlow<Int?>(null)
    private val patientCode = MutableStateFlow<String?>(null)
    private var notPlannedActivities = MutableStateFlow<List<HomeCareUnplannedActivity>>(listOf())
    private var transferActivity: HomeCareUnplannedActivity? = null

    private val unplannedActivities = combine(notPlannedActivities,transferActivityCode){ notPlannedActivities,transferActivityCode ->
        val otherActivities = notPlannedActivities.filter { it.code != transferActivityCode }
        val activities = notPlannedActivities.firstOrNull{ it.code == transferActivityCode}?.let { transferActivity ->
            this.transferActivity = transferActivity
            listOf(transferActivity) + otherActivities
        } ?: run{
            otherActivities
        }
        activities.map {
            SelectCareActivityPopupUIState.ActivityListItem(it.desc, it.id, false,it.code == transferActivityCode)
        }
    }

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
        unplannedActivities,
        plannedActivities
    ) { isLoading, errorMessage, query, notPlannedActivities, plannedActivities ->
        val unPlannedItems = if (query.isNotEmpty()) {
            notPlannedActivities.filter { it.title.contains(query, true) }
        } else {
            notPlannedActivities
        }

        val plannedItems = plannedActivities?.map { SelectCareActivityPopupUIState.ActivityListItem(it.type, it.id, false,false) } ?: listOf()

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

    fun sendTransferActivity(duration:Long,onSuccess:() -> Unit){
        viewModelScope.launch(coroutineExceptionHandler) {
            transferActivity?.let { activity ->
                val item = HomeCareActivitySave(
                    codCase = patientCode.value ?: "",
                    idActivityType = activity.id,
                    execDateTime = Date().format("yyyy.MM.dd HH:mm"),
                    duration = duration.toInt(),
                    notes = "",
                    showInDiary = false,
                    idPlanning = carePlanId.value
                )
                val res = homeCareActivitiesRepository.addHomeCareActivity(item)
                res.error?.desc?.let {
                    errorMessage.value = it

                } ?: run{
                    onSuccess()
                }
            }

        }
    }
}