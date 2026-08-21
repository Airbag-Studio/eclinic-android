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
    val sections: List<CarePlanSection>,
    val cares: List<CarePlanCoursesListItem>
)

/**
 * Una sezione del riepilogo del piano di cura. Il tipo determina la resa: le sezioni con
 * dati strutturati (NIC, NOC, prestazioni pianificate) non vengono appiattite in un unico
 * testo, così la UI può dare a codice, descrizione e scala il peso visivo che meritano.
 */
sealed interface CarePlanSection {
    val titleId: Int

    data class Text(override val titleId: Int, val text: String) : CarePlanSection

    data class Bullets(
        override val titleId: Int,
        val items: List<String>,
        val note: String?
    ) : CarePlanSection

    data class CodedItems(
        override val titleId: Int,
        val items: List<CodedItem>
    ) : CarePlanSection

    data class Planned(
        override val titleId: Int,
        val items: List<PlannedActivityRow>
    ) : CarePlanSection
}

data class CodedItem(val code: String, val description: String, val scale: String? = null)

data class PlannedActivityRow(val title: String, val meta: List<String>)

/**
 * Aggiunge la sezione solo se ha un elenco o un'annotazione: un'annotazione senza voci
 * resta comunque visibile, perché è contenuto inserito dall'operatore (TS1-2).
 */
private fun MutableList<CarePlanSection>.addBulletsSection(
    titleId: Int,
    items: List<String>,
    note: String?
) {
    val remarks = note?.takeIf { it.isNotBlank() }
    if (items.isEmpty() && remarks == null) return
    add(CarePlanSection.Bullets(titleId, items, remarks))
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
            val plannedRows = planndeActivities.map { activity ->
                PlannedActivityRow(
                    title = activity.type,
                    meta = buildList {
                        val number = if (activity.number > 0) {
                            activity.number.toString()
                        } else {
                            "Su Necessità"
                        }
                        add("$number ${activity.timeUnit}")
                        getWeekDays(activity.weekDays).takeIf { it.isNotEmpty() }?.let { add(it) }
                        add("Durata (min):${activity.duration}")
                        activity.qualMin.takeIf { it.isNotEmpty() }?.let { add(it) }
                        activity.notes.takeIf { it.isNotEmpty() }?.let { add(it) }
                    }
                )
            }

            // Solo le sezioni che hanno davvero contenuto: un piano senza problema o
            // obiettivo non si porta dietro intestazioni vuote (TS1-6)
            val sections = buildList {
                selectedPan?.diagnosis?.takeIf { it.isNotBlank() }?.let {
                    add(CarePlanSection.Text(R.string.diagnosis, it))
                }
                selectedPan?.problemDescription?.takeIf { it.isNotBlank() }?.let {
                    add(CarePlanSection.Text(R.string.problem, it))
                }
                addBulletsSection(
                    titleId = R.string.defining_features,
                    items = selectedPan?.definingFeatures?.map { it.name }.orEmpty(),
                    note = selectedPan?.definingFeaturesRemarks
                )
                addBulletsSection(
                    titleId = R.string.related_factors,
                    items = selectedPan?.relatedFactors?.map { it.name }.orEmpty(),
                    note = selectedPan?.relatedFactorsRemarks
                )
                selectedPan?.goal?.takeIf { it.isNotBlank() }?.let {
                    add(CarePlanSection.Text(R.string.goal, it))
                }
                // NIC e NOC chiudono il riepilogo, prima delle prestazioni pianificate (TS1-1)
                selectedPan?.nicActivities?.takeIf { it.isNotEmpty() }?.let { activities ->
                    add(
                        CarePlanSection.CodedItems(
                            titleId = R.string.nic_activities,
                            items = activities.map { CodedItem(it.code, it.description) }
                        )
                    )
                }
                selectedPan?.nocIndicators?.takeIf { it.isNotEmpty() }?.let { indicators ->
                    add(
                        CarePlanSection.CodedItems(
                            titleId = R.string.noc_indicators,
                            items = indicators.map {
                                CodedItem(it.code, it.description, it.scale.takeIf { s -> s.isNotBlank() })
                            }
                        )
                    )
                }
                plannedRows.takeIf { it.isNotEmpty() }?.let {
                    add(CarePlanSection.Planned(R.string.planned_activities, it))
                }
            }

            CarePlanDetailsUIState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                title = selectedPan?.title ?: "",
                date = selectedPan?.openDate?.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: "",
                cares = activities ?: listOf(),
                sections = sections
            )
        } catch (e: Throwable) {
            this.errorMessage.value = e.localizedMessage

            CarePlanDetailsUIState(
                isLoading = false,
                errorMessage = e.localizedMessage,
                title = "",
                date = "",
                cares = listOf(),
                sections = listOf()
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
            sections = listOf()
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