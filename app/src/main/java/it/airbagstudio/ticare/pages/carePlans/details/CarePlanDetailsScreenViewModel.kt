package it.airbagstudio.ticare.pages.carePlans.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCareActivity
import ch.ticare.eclinic.library.entity.HomeCarePlan
import ch.ticare.eclinic.library.entity.HomeCarePlannedActivity
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.streams.toList

data class CarePlanDetailsUIState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val title: String,
    val date: String,
    val textItem: List<TextItems>,
    val cares: List<CarePlanCoursesListItem>

) {
    data class TextItems(
        val titleStringId: Int,
        val content: String
    )
}

@HiltViewModel
class CarePlanDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
    private val userRepository: UserDetailRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
) : ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val planId: String = savedStateHandle[DestinationsArgs.ID]!!

    private val isLoading = MutableStateFlow(false)
    private val plans = MutableStateFlow<List<HomeCarePlan>>(listOf())
    private val errorMessage = MutableStateFlow<String?>(null)
    private val activities = MutableStateFlow<List<HomeCareActivity>?>(null)
    private val selectedPan = MutableStateFlow<HomeCarePlan?>(null)
    private val selectedDate =
        userRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
    private val shift = userRepository.getCurrentShift()
    private val planndeActivities = MutableStateFlow<List<HomeCarePlannedActivity>>(listOf())
    private val modifiedIds = MutableStateFlow<List<String>>(listOf())

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            isLoading.value = false
            errorMessage.value = throwable.localizedMessage
        }

    val uiState = combine(
        isLoading,
        errorMessage,
        activities,
        selectedPan,
        planndeActivities
    ) { isLoading, errorMessage, homeCareActivities, selectedPan, planndeActivities ->
        try {
            val activities = homeCareActivities?.map {
                CarePlanCoursesListItem(
                    title = it.type,
                    executed = true,
                    id = it.id,
                    planned = it.isScheduled,
                    activity = it,
                    isLocalContent = it.user.isEmpty()
                )
            }
            val plannedInfo = planndeActivities.map {
                var data = "${it.type}\n${if(it.number > 0) it.number.toString() else "Su Necessità"} ${it.timeUnit}\n${getWeekDays(it.weekDays)}"
                if(it.qualMin.isNotEmpty()){
                    data+= "\n${it.qualMin}"
                }
                if (it.notes.isNotEmpty()){
                    data+= "\n${it.notes}"
                }
                data
            }.joinToString("\n\n")

            val textItems = listOf(

                CarePlanDetailsUIState.TextItems(R.string.diagnosis, selectedPan?.diagnosis ?: ""),
                CarePlanDetailsUIState.TextItems(
                    R.string.problem,
                    selectedPan?.problemDescription ?: ""
                ),
                CarePlanDetailsUIState.TextItems(
                    R.string.defining_features,
                    selectedPan?.definingFeatures?.map { it.name }?.joinToString("\n") ?: ""
                ),
                CarePlanDetailsUIState.TextItems(
                    R.string.related_factors,
                    selectedPan?.relatedFactors?.map { it.name }?.joinToString("\n") ?: ""
                ),
                CarePlanDetailsUIState.TextItems(R.string.goal, selectedPan?.goal ?: ""),
                CarePlanDetailsUIState.TextItems(
                    R.string.planned_activities,
                    plannedInfo
                )
            )



            CarePlanDetailsUIState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                title = selectedPan?.title ?: "",
                date = selectedPan?.openDate?.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: "",
                cares = activities ?: listOf(),
                textItem = textItems
            )
        } catch (e: Throwable) {
            this.errorMessage.value = e.localizedMessage

            CarePlanDetailsUIState(
                isLoading = false,
                errorMessage = e.localizedMessage,
                title = "",
                date = "",
                cares = listOf(),
                textItem = listOf()
            )

        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CarePlanDetailsUIState(
            isLoading = true,
            errorMessage = null,
            title = "",
            date = "",
            cares = listOf(),
            textItem = listOf()
        )
    )

    init {
        downloadData()
    }

    private fun getWeekDays(string: String): String {
        val chars = string.toCharArray().map { it.digitToInt() }
        if (chars.all { it == 1 }) {
            return "Tutti i giorni"
        } else if (chars.all { it == 0 }) {
            return ""
        } else {
            val concatDays: MutableList<String> = mutableListOf()
            chars.forEachIndexed { index, element ->
                if (element == 1) {
                    concatDays.add(
                        DayOfWeek(index + 1).getDisplayName(
                            TextStyle.SHORT,
                            Locale.ITALIAN
                        )
                    )
                }
            }
            return concatDays.joinToString(" | ")
        }
    }

    fun downloadData() {
        clearError()
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            modifiedIds.value = offlineOnlineRepository.getModifiedIdForSection(patientCod,OfflineSection.HomeCareActivities)
            val res = homeCareActivitiesRepository.getHomeCarePlans(patientCod)
            val intPlanId = planId.toIntOrNull() ?: return@launch
            if (res.status == "success") {
                plans.value = res.results ?: listOf()
                selectedPan.value = plans.value.firstOrNull { it.id == intPlanId }
                val date = selectedDate.format("yyyy.MM.dd")
                val shiftIndex: Int? =
                    if (shift != null) {
                        userRepository.getOperatingShifts().results?.let { shifts ->
                            shifts.indexOf(shift)
                        } ?: run {
                            null
                        }
                    } else {
                        null
                    }
                activities.value = homeCareActivitiesRepository.getHomeCareActivities(
                    code = patientCod,
                    start = date,
                    end = date,
                    shift = shiftIndex,
                    carePlanId = selectedPan.value?.id ?: 0
                ).results

                planndeActivities.value = homeCareActivitiesRepository.getHomeCareActivitiesPlanned(
                    patientCod,
                    intPlanId
                ).results ?: listOf()
            } else if (res.error != null) {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false
        }
    }

    fun clearError() {
        errorMessage.value = null
    }
}