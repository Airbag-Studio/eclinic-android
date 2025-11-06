package it.airbagstudio.ticare.pages.carePlans.selectActivity

import android.R.attr.duration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCareActivitySave
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.Date
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.roundToInt

data class SelectCareActivityPopupUIState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val query: String,
    val unplannedActivities: List<ActivityListItem>,
    val plannedActivities: List<ActivityListItem>


){
    data class ActivityListItem(
        val title:String,
        val code: String?,
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
            SelectCareActivityPopupUIState.ActivityListItem(it.desc,it.code, it.id, false,it.code == transferActivityCode)
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
            notPlannedActivities.filter { it.title.contains(query, true) || it.code?.contains(query,true) == true }
        } else {
            notPlannedActivities
        }

        val plannedItems = plannedActivities?.map { SelectCareActivityPopupUIState.ActivityListItem(it.type,null, it.id, false,false) } ?: listOf()

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
        CoroutineExceptionHandler { _, throwable ->
            errorMessage.value = throwable.localizedMessage
            isLoading.value = false
        }

    fun setCarePlanId(id: Int?) {
        carePlanId.value = id
    }

    fun setPatientCode(code: String) {
        patientCode.value = code
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = homeCareActivitiesRepository.getHomeCareActivitiesUnplanned(code)
            notPlannedActivities.value = res.results ?: listOf()
            errorMessage.value = res.error?.desc
            isLoading.value = false
        }
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

    fun executeAllPlannedActivities(elapsedTimeFromLastActivity : Long?, onSuccess:() -> Unit){
        val elapsedTime = elapsedTimeFromLastActivity ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            plannedActivities.collect { activities ->
                val totalPlannedTime: Double = (activities?.sumOf { it.duration } ?: 0).toDouble()
                var cumulatedExecutionTime = 0
                val activitiesToSend = mutableListOf<HomeCareActivitySave>()
                val startTime = Date()
                activities?.forEach { activity ->
                    val plannedTime: Double = activity.duration.toDouble()
                    val executionTime : Int = if(plannedTime > 0) ((plannedTime * elapsedTime) / totalPlannedTime).roundToInt() else 0
                    val activityTime: Long = startTime.time + (cumulatedExecutionTime.toLong() * 1000 * 60)
                    cumulatedExecutionTime += executionTime
                    val activityToSave = HomeCareActivitySave(
                        idPlanning = activity.id,
                        idActivityType = null,
                        codCase = patientCode.value ?: "",
                        execDateTime = Date( activityTime).format("yyyy.MM.dd HH:mm"),
                        duration = executionTime,
                        notes = activity.notes,
                        showInDiary = false,
                    )
                    activitiesToSend.add(activityToSave)
                }
                var sortedActivities: MutableList<HomeCareActivitySave> = activitiesToSend.sortedBy { it.execDateTime }.toMutableList()
                if(cumulatedExecutionTime != elapsedTime.toInt()){
                    val remainingTime = elapsedTime.toInt() - cumulatedExecutionTime
                    val duration = sortedActivities[sortedActivities.lastIndex].duration
                    sortedActivities[sortedActivities.lastIndex] = sortedActivities[sortedActivities.lastIndex].copy(duration = duration + remainingTime)
                }
                //Log.w("executeAllPlannedActivities","elapsedTime $elapsedTime totalPlannedTime $totalPlannedTime sortedActivities $sortedActivities")
                //Log.w("EXECUTING",sortedActivities.map { it.duration }.toString())
                sortedActivities.forEach {
                    saveActivity(it)
                }
                onSuccess()
            }

        }

    }

    suspend fun saveActivity(activity: HomeCareActivitySave){
        val res = homeCareActivitiesRepository.addHomeCareActivity(activity)
        res.error?.desc?.let {
            errorMessage.value = it
        }
    }
}