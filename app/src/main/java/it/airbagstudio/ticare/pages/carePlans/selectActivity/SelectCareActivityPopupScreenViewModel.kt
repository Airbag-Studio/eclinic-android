package it.airbagstudio.ticare.pages.carePlans.selectActivity

import android.util.Log
import android.util.Log.i
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.roundToInt

data class SelectCareActivityPopupUIState(
    val isLoading: Boolean,
    val query: String,
    val unplannedActivities: List<ActivityListItem>,
    val plannedActivities: List<ActivityListItem>


){
    data class ActivityListItem(
        val title:String,
        val code: String?,
        val id: Int,
        val isPlanned: Boolean,
        val isTransferActivity: Boolean,
        val isSelected: Boolean
    )
}

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
) : ViewModel() {

    private val transferActivityCode = homeCareActivitiesRepository.getTransferActivityCode()
    private val isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow<String>("")
    private val carePlanId = MutableStateFlow<Int?>(null)
    private val patientCode = MutableStateFlow<String?>(null)
    private var notPlannedActivities = MutableStateFlow<List<HomeCareUnplannedActivity>>(listOf())

    private var plannedActivities = MutableStateFlow<List< HomeCarePlannedActivity>>(listOf())

    private var transferActivity: HomeCareUnplannedActivity? = null

    private var selectedActivityIds: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())

    var errorMessage = _errorMessage.asStateFlow()


    private val unplannedActivities = combine(notPlannedActivities,transferActivityCode,selectedActivityIds){ notPlannedActivities,transferActivityCode,selectedActivityIds ->
        val otherActivities = notPlannedActivities.filter { it.code != transferActivityCode }
        val activities = notPlannedActivities.firstOrNull{ it.code == transferActivityCode}?.let { transferActivity ->
            this.transferActivity = transferActivity
            listOf(transferActivity) + otherActivities
        } ?: run{
            otherActivities
        }
        activities.map {
            SelectCareActivityPopupUIState.ActivityListItem(it.desc,it.code, it.id, false,it.code == transferActivityCode, isSelected = selectedActivityIds.contains(it.id))
        }
    }


    val uiState = combine(
        isLoading,
        selectedActivityIds,
        query,
        unplannedActivities,
        plannedActivities
    ) { isLoading, selectedActivityIds, query, notPlannedActivities, plannedActivities ->
        val unPlannedItems = if (query.isNotEmpty()) {
            notPlannedActivities.filter { it.title.contains(query, true) || it.code?.contains(query,true) == true }
        } else {
            notPlannedActivities
        }

        val plannedItems = plannedActivities?.map { SelectCareActivityPopupUIState.ActivityListItem(it.type,null, it.id, false,false, isSelected = selectedActivityIds.contains(it.id)) } ?: listOf()

        SelectCareActivityPopupUIState(
            isLoading = isLoading,
            query = query,
            unplannedActivities = unPlannedItems,
            plannedActivities = plannedItems
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = SelectCareActivityPopupUIState(
            isLoading = true,
            query = "",
            unplannedActivities = listOf(),
            plannedActivities = listOf()
        )
    )

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _errorMessage.value = throwable.localizedMessage
            isLoading.value = false
        }

    fun loadData(carePlanId: Int?, patientCode: String){
        this.carePlanId.value = carePlanId
        this.patientCode.value = patientCode
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = homeCareActivitiesRepository.getHomeCareActivitiesUnplanned(patientCode)
            if (carePlanId != null) {
                val plannedRes = homeCareActivitiesRepository.getHomeCareActivitiesPlanned(
                    patientCode,
                    carePlanId
                ).results
                plannedActivities.value = plannedRes ?: listOf()
            }
            notPlannedActivities.value = res.results ?: listOf()
            _errorMessage.value = res.error?.desc
            isLoading.value = false
        }
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun clearErrors() {
        _errorMessage.value = null
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
                    _errorMessage.value = it

                } ?: run{
                    onSuccess()
                }
            }

        }
    }

    fun executeAllPlannedActivities(elapsedTimeFromLastActivity : Long?, onSuccess:() -> Unit){
        val elapsedTime = elapsedTimeFromLastActivity ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            val startTime = Date().time - (elapsedTimeFromLastActivity * 1000 * 60)
            launch {
                plannedActivities.value.filter { ac -> selectedActivityIds.value.contains(ac.id) }.sortedBy { it.duration }.let { activities ->
                    if (activities.isEmpty()) return@launch
                    val totalPlannedTime: Double =
                        (activities.sumOf { it.duration }).toDouble()
                    var cumulatedExecutionTime = 0
                    val activitiesToSend = mutableListOf<HomeCareActivitySave>()
                    val currentTime = Date().time

                    for (activity in activities) {
                        val plannedTime: Double = activity.duration.toDouble()
                        var executionTime: Int =
                            if (plannedTime > 0) {
                                val calculated = ((plannedTime * elapsedTime) / totalPlannedTime).roundToInt()
                                // Se il calcolo dà 0 ma la duration originale NON è 0, metti 1
                                if (calculated == 0) 1 else calculated
                            } else {
                                // Se la duration originale è 0, la duration calcolata è 0
                                0
                            }
                        cumulatedExecutionTime += executionTime
                        val activityTime: Long = startTime + (cumulatedExecutionTime.toLong() * 1000 * 60)
                        
                        // Controlla se activityTime è nel futuro
                        if (activityTime > currentTime) {
                            // Ricalcola la duration per questa attività: tempo disponibile fino ad ora
                            val previousCumulatedTime = cumulatedExecutionTime - executionTime
                            val availableTime = ((currentTime - (startTime + (previousCumulatedTime.toLong() * 1000 * 60))) / 60000).toInt()
                            
                            val activityToSave = HomeCareActivitySave(
                                idPlanning = activity.id,
                                idActivityType = null,
                                codCase = patientCode.value ?: "",
                                execDateTime = Date(currentTime).format("yyyy.MM.dd HH:mm"),
                                duration = availableTime.coerceAtLeast(0),
                                notes = activity.notes,
                                showInDiary = false,
                            )
                            activitiesToSend.add(activityToSave)
                            // Stop: non processare altre attività
                            break
                        }
                        
                        val activityToSave = HomeCareActivitySave(
                            idPlanning = activity.id,
                            idActivityType = null,
                            codCase = patientCode.value ?: "",
                            execDateTime = Date(activityTime).format("yyyy.MM.dd HH:mm"),
                            duration = executionTime,
                            notes = activity.notes,
                            showInDiary = false,
                        )
                        activitiesToSend.add(activityToSave)
                    }
                    
                    val sortedActivities: MutableList<HomeCareActivitySave> =
                        activitiesToSend.sortedBy { it.execDateTime }.toMutableList()
                    
                    Log.d("sortedActivities", sortedActivities.toString())
                    sortedActivities.forEach {
                        saveActivity(it)
                    }
                    onSuccess()
                }
            }
        }
    }

    fun executeAllUnplannedActivities(elapsedTimeFromLastActivity : Long?, onSuccess:() -> Unit) {
        val elapsedTime = elapsedTimeFromLastActivity ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            val startTime = Date().time - (elapsedTimeFromLastActivity * 1000 * 60)
            notPlannedActivities.value.filter { ac -> selectedActivityIds.value.contains(ac.id) }.sortedBy { it.duration }.let{ activities ->
                if (activities.isEmpty()) return@launch

                val totalPlannedTime: Double = (activities.sumOf { it.duration }).toDouble()
                var cumulatedExecutionTime = 0
                val activitiesToSend = mutableListOf<HomeCareActivitySave>()
                val currentTime = Date().time

                for (activity in activities) {
                    val plannedTime: Double = activity.duration.toDouble()
                    var executionTime: Int =
                        if (plannedTime > 0) {
                            val calculated = ((plannedTime * elapsedTime) / totalPlannedTime).roundToInt()
                            // Se il calcolo dà 0 ma la duration originale NON è 0, metti 1
                            if (calculated == 0) 1 else calculated
                        } else {
                            // Se la duration originale è 0, la duration calcolata è 0
                            0
                        }
                    cumulatedExecutionTime += executionTime
                    val activityTime: Long = startTime + (cumulatedExecutionTime.toLong() * 1000 * 60)
                    
                    // Controlla se activityTime è nel futuro
                    if (activityTime > currentTime) {
                        // Ricalcola la duration per questa attività: tempo disponibile fino ad ora
                        val previousCumulatedTime = cumulatedExecutionTime - executionTime
                        val availableTime = ((currentTime - (startTime + (previousCumulatedTime.toLong() * 1000 * 60))) / 60000).toInt()
                        
                        val activityToSave = HomeCareActivitySave(
                            idActivityType = activity.id,
                            codCase = patientCode.value ?: "",
                            execDateTime = Date(currentTime).format("yyyy.MM.dd HH:mm"),
                            duration = availableTime.coerceAtLeast(0),
                            notes = "",
                            showInDiary = true,
                        )
                        activitiesToSend.add(activityToSave)
                        // Stop: non processare altre attività
                        break
                    }
                    
                    val activityToSave = HomeCareActivitySave(
                        idActivityType = activity.id,
                        codCase = patientCode.value ?: "",
                        execDateTime = Date(activityTime).format("yyyy.MM.dd HH:mm"),
                        duration = executionTime,
                        notes = "",
                        showInDiary = true,
                    )
                    activitiesToSend.add(activityToSave)
                }
                
                val sortedActivities: MutableList<HomeCareActivitySave> = activitiesToSend.sortedBy { it.execDateTime }.toMutableList()
                
                Log.d("sortedActivities", sortedActivities.toString())
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
            _errorMessage.value = it
        }
    }

    fun changeActivitySelection(id:Int, isSelected:Boolean){
        val selected = selectedActivityIds.value.toMutableList()
        if(isSelected && !selected.contains(id)){
            selected.add(id)
        }else if(!isSelected && selected.contains(id)){
            selected.remove(id)
        }
        selectedActivityIds.value = selected
    }

    fun selectAllPlanned(){
        val plannedIds = plannedActivities.value.map { it.id }
        selectedActivityIds.value = plannedIds
    }

    fun selectAllUnplanned(){
        val plannedIds = notPlannedActivities.value.map { it.id }
        selectedActivityIds.value = plannedIds
    }

    fun cancelAllSection(){
        selectedActivityIds.value = listOf()
    }
}
