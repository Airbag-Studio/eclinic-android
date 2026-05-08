package it.airbagstudio.ticare.pages.carePlans.selectActivity

import android.R.attr.duration
import android.util.Log
import android.util.Log.i
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCareActivity
import ch.ticare.eclinic.library.entity.HomeCareActivitySave
import ch.ticare.eclinic.library.entity.HomeCarePlannedActivity
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.UserMarkingRepository
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


data class SelectCareActivityPopupUIState(
    val isLoading: Boolean,
    val query: String,
    val unplannedActivities: List<ActivityListItem>,
    val plannedActivities: List<ActivityListItem>


) {
    data class ActivityListItem(
        val title: String,
        val code: String?,
        val id: Int,
        val carePlanId: Int?,
        val isPlanned: Boolean,
        val isTransferActivity: Boolean,
        val isSelected: Boolean
    )
}

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
    private val userMarkingRepository: UserMarkingRepository

) : ViewModel() {

    private val transferActivityCode = homeCareActivitiesRepository.getTransferActivityCode()
    private val isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow<String>("")
    private val carePlanId = MutableStateFlow<Int?>(null)
    private val patientCode = MutableStateFlow<String?>(null)
    private var notPlannedActivities = MutableStateFlow<List<HomeCareUnplannedActivity>>(listOf())

    private var plannedActivities = MutableStateFlow<List<HomeCarePlannedActivity>>(listOf())

    private var transferActivity: HomeCareUnplannedActivity? = null

    private var selectedActivityIds: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())

    var errorMessage = _errorMessage.asStateFlow()


    private val unplannedActivities = combine(
        notPlannedActivities,
        transferActivityCode,
        selectedActivityIds
    ) { notPlannedActivities, transferActivityCode, selectedActivityIds ->
        val otherActivities = notPlannedActivities.filter { it.code != transferActivityCode }
        val activities = notPlannedActivities.firstOrNull { it.code == transferActivityCode }
            ?.let { transferActivity ->
                this.transferActivity = transferActivity
                listOf(transferActivity) + otherActivities
            } ?: run {
            otherActivities
        }
        activities.map {
            SelectCareActivityPopupUIState.ActivityListItem(
                it.desc,
                it.code,
                it.id,
                carePlanId = null,
                false,
                it.code == transferActivityCode,
                isSelected = selectedActivityIds.contains(it.id)
            )
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
            notPlannedActivities.filter {
                it.title.contains(query, true) || it.code?.contains(
                    query,
                    true
                ) == true
            }
        } else {
            notPlannedActivities
        }

        val plannedItems = plannedActivities?.map {
            SelectCareActivityPopupUIState.ActivityListItem(
                it.type,
                null,
                it.id,
                carePlanId = it.carePlan,
                false,
                false,
                isSelected = selectedActivityIds.contains(it.id)
            )
        } ?: listOf()

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

    fun loadData(carePlanId: Int?, patientCode: String) {
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
            } else {
                val carePlanIds =
                    homeCareActivitiesRepository.getHomeCarePlans(patientCode).results?.map { it.id }
                        ?: listOf()
                val plannedRes = homeCareActivitiesRepository.getHomeCareActivitiesPlanned(
                    patientCode,
                    null
                ).results?.filter { carePlanIds.contains(it.carePlan) } ?: listOf()
                plannedActivities.value = plannedRes
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

    fun sendTransferActivity(onSuccess: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userMarkingRepository.getMinutesFromLastActivityOnce()?.let { minutesFromLastActivity ->
                transferActivity?.let { activity ->
                    val item = HomeCareActivitySave(
                        codCase = patientCode.value ?: "",
                        idActivityType = activity.id,
                        execDateTime = Date().format("yyyy.MM.dd HH:mm"),
                        duration = minutesFromLastActivity.toInt(),
                        notes = "",
                        showInDiary = false,
                        idPlanning = carePlanId.value
                    )
                    val res = homeCareActivitiesRepository.addHomeCareActivity(item)
                    res.error?.desc?.let {
                        _errorMessage.value = it

                    } ?: run {
                        onSuccess()
                    }
                }
            }

        }
    }

    fun executeAllPlannedActivities(onSuccess: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userMarkingRepository.getMinutesFromLastActivityOnce()?.let { elapsedTimeFromLastActivity ->
                    val elapsedTime = elapsedTimeFromLastActivity
                    val startTime = Date().time - (elapsedTime * 1000 * 60)
                    launch {
                        val activities = plannedActivities.value
                            .filter { selectedActivityIds.value.contains(it.id) }
                        if (activities.isEmpty()) return@launch

                        val executionTimes =
                            distributeTime(activities.map { it.duration }, elapsedTime)
                        var lastEndTime = startTime

                        val activitiesToSend =
                            activities.zip(executionTimes).map { (activity, executionTime) ->
                                val endTime = lastEndTime + executionTime.toLong() * 60000
                                lastEndTime = endTime
                                HomeCareActivitySave(
                                    idPlanning = activity.id,
                                    idActivityType = null,
                                    codCase = patientCode.value ?: "",
                                    execDateTime = Date(endTime).format("yyyy.MM.dd HH:mm"),
                                    duration = executionTime,
                                    notes = activity.notes,
                                    showInDiary = false,
                                )
                            }

                        Log.d("sortedActivities", activitiesToSend.toString())
                        activitiesToSend.sortedBy { it.execDateTime }.forEach { saveActivity(it) }
                        onSuccess()
                    }
                }
        }
    }

    fun executeAllUnplannedActivities(onSuccess: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userMarkingRepository.getMinutesFromLastActivityOnce()
                ?.let { elapsedTimeFromLastActivity ->
                    val elapsedTime = elapsedTimeFromLastActivity
                    val startTime = Date().time - (elapsedTime * 1000 * 60)
                    val activities = notPlannedActivities.value
                        .filter { selectedActivityIds.value.contains(it.id) }
                        .sortedBy { it.code.toIntOrNull() ?: Int.MAX_VALUE }
                    if (activities.isEmpty()) return@launch

                    val executionTimes = distributeTime(activities.map { it.duration }, elapsedTime)
                    var lastEndTime = startTime

                    val activitiesToSend =
                        activities.zip(executionTimes).map { (activity, executionTime) ->
                            val endTime = lastEndTime + executionTime.toLong() * 60000
                            lastEndTime = endTime
                            HomeCareActivitySave(
                                idActivityType = activity.id,
                                codCase = patientCode.value ?: "",
                                execDateTime = Date(endTime).format("yyyy.MM.dd HH:mm"),
                                duration = executionTime,
                                notes = "",
                                showInDiary = true,
                            )
                        }

                    Log.d("sortedActivities", activitiesToSend.toString())
                    activitiesToSend.sortedBy { it.execDateTime }.forEach { saveActivity(it) }
                    onSuccess()
                }
        }
    }

    private fun distributeTime(durations: List<Int>, elapsedTime: Long): List<Int> {
        val total = durations.sum().toDouble()
        if (total == 0.0) return durations.map { 0 }

        val exact = durations.map { it.toDouble() * elapsedTime / total }
        val floors = exact.map { it.toInt() }
        val remainder = elapsedTime.toInt() - floors.sum()

        val result = floors.toMutableList()
        exact.indices
            .sortedByDescending { exact[it] - floors[it] }
            .take(remainder)
            .forEach { result[it]++ }

        return result
    }

    suspend fun saveActivity(activity: HomeCareActivitySave) {
        val res = homeCareActivitiesRepository.addHomeCareActivity(activity)
        res.error?.desc?.let {
            _errorMessage.value = it
        }
    }

    fun changeActivitySelection(id: Int, isSelected: Boolean) {
        val selected = selectedActivityIds.value.toMutableList()
        if (isSelected && !selected.contains(id)) {
            selected.add(id)
        } else if (!isSelected && selected.contains(id)) {
            selected.remove(id)
        }
        selectedActivityIds.value = selected
    }

    fun selectAllPlanned() {
        val plannedIds = plannedActivities.value.map { it.id }
        selectedActivityIds.value = plannedIds
    }

    fun selectAllUnplanned() {
        val plannedIds = notPlannedActivities.value.map { it.id }
        selectedActivityIds.value = plannedIds
    }

    fun cancelAllSection() {
        selectedActivityIds.value = listOf()
    }
}
