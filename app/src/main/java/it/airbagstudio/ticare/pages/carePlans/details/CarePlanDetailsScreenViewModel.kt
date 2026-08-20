package it.airbagstudio.ticare.pages.carePlans.details

import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
        val content: AnnotatedString
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
        CoroutineExceptionHandler { _, throwable ->
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
            val plannedActivitiesAnnotated = planndeActivities.map {
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(it.type)
                    }
                    append("\n${if (it.number > 0) it.number.toString() else "Su Necessità"} ${it.timeUnit}\n")
                    val weekDays = getWeekDays(it.weekDays)
                    if (weekDays.isNotEmpty()) append("$weekDays \n")
                    append("Durata (min):${it.duration}")
                    if (it.qualMin.isNotEmpty()) append("\n${it.qualMin}")
                    if (it.notes.isNotEmpty()) append("\n${it.notes}")
                }
            }
            val plannedInfo = buildAnnotatedString {
                plannedActivitiesAnnotated.forEachIndexed { index, item ->
                    append(item)
                    if (index < plannedActivitiesAnnotated.lastIndex) append("\n\n")
                }
            }

            // Le annotazioni chiudono la sezione a cui si riferiscono, in corsivo per
            // distinguerle dalle voci elencate sopra (TS1-2)
            fun withRemarks(items: List<String>, remarks: String) = buildAnnotatedString {
                append(items.joinToString("\n"))
                if (remarks.isNotEmpty()) {
                    if (items.isNotEmpty()) append("\n\n")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(remarks)
                    }
                }
            }

            val definingFeaturesInfo = withRemarks(
                items = selectedPan?.definingFeatures?.map { it.name }.orEmpty(),
                remarks = selectedPan?.definingFeaturesRemarks.orEmpty()
            )
            val relatedFactorsInfo = withRemarks(
                items = selectedPan?.relatedFactors?.map { it.name }.orEmpty(),
                remarks = selectedPan?.relatedFactorsRemarks.orEmpty()
            )

            val nicActivities = selectedPan?.nicActivities.orEmpty()
            val nicInfo = buildAnnotatedString {
                nicActivities.forEachIndexed { index, activity ->
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(activity.code)
                    }
                    append("\n${activity.description}")
                    if (index < nicActivities.lastIndex) append("\n\n")
                }
            }

            val nocIndicators = selectedPan?.nocIndicators.orEmpty()
            val nocInfo = buildAnnotatedString {
                nocIndicators.forEachIndexed { index, indicator ->
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(indicator.code)
                    }
                    append("\n${indicator.description}")
                    if (indicator.scale.isNotEmpty()) append("\nScala: ${indicator.scale}")
                    if (index < nocIndicators.lastIndex) append("\n\n")
                }
            }

            val textItems = buildList {
                add(CarePlanDetailsUIState.TextItems(R.string.diagnosis, AnnotatedString(selectedPan?.diagnosis ?: "")))
                add(
                    CarePlanDetailsUIState.TextItems(
                        R.string.problem,
                        AnnotatedString(selectedPan?.problemDescription ?: "")
                    )
                )
                add(
                    CarePlanDetailsUIState.TextItems(
                        R.string.defining_features,
                        definingFeaturesInfo
                    )
                )
                add(
                    CarePlanDetailsUIState.TextItems(
                        R.string.related_factors,
                        relatedFactorsInfo
                    )
                )
                add(CarePlanDetailsUIState.TextItems(R.string.goal, AnnotatedString(selectedPan?.goal ?: "")))
                // NIC e NOC chiudono il riepilogo, prima delle prestazioni pianificate (TS1-1).
                // Compaiono solo se il piano ha elementi collegati, per non lasciare
                // due intestazioni vuote sui piani che non ne hanno.
                if (nicActivities.isNotEmpty()) {
                    add(CarePlanDetailsUIState.TextItems(R.string.nic_activities, nicInfo))
                }
                if (nocIndicators.isNotEmpty()) {
                    add(CarePlanDetailsUIState.TextItems(R.string.noc_indicators, nocInfo))
                }
                add(
                    CarePlanDetailsUIState.TextItems(
                        R.string.planned_activities,
                        plannedInfo
                    )
                )
            }



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
                    intPlanId,
                    skipQualification = true
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