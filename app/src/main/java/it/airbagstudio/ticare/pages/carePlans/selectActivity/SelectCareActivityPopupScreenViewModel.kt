package it.airbagstudio.ticare.pages.carePlans.selectActivity

import android.R.attr.duration
import android.util.Log
import android.util.Log.i
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.BillingMode
import ch.ticare.eclinic.library.entity.HomeCareActivity
import ch.ticare.eclinic.library.entity.HomeCareActivitySave
import ch.ticare.eclinic.library.entity.HomeCarePlannedActivity
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.UserMarkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


data class SelectCareActivityPopupUIState(
    val isLoadingUnplanned: Boolean,
    val isLoadingPlanned: Boolean,
    val query: String,
    val unplannedSections: List<ActivitySection>,
    val plannedSections: List<ActivitySection>,
    val plannedSelection: PlannedSelection,
    val selectedCount: Int
) {
    /** Stato del "seleziona tutte" delle pianificate, per la checkbox a tre stati. */
    enum class PlannedSelection { NONE, SOME, ALL }

    data class ActivityListItem(
        val title: String,
        val code: String?,
        val id: Int,
        val carePlanId: Int?,
        val isPlanned: Boolean,
        val isTransferActivity: Boolean,
        val isSelected: Boolean
    )

    // Le prestazioni sono raggruppate per Modalità di fatturazione (TS1-4).
    // Con title e titleRes entrambi null il blocco non ha intestazione: è il caso
    // della riga di trasferta, che resta in cima e fuori dai raggruppamenti.
    data class ActivitySection(
        val title: String? = null,
        val titleRes: Int? = null,
        val items: List<ActivityListItem>
    )
}

@HiltViewModel
class SelectCareActivityPopupScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
    private val userMarkingRepository: UserMarkingRepository

) : ViewModel() {

    // Le due liste arrivano da chiamate distinte: ognuna ha il suo stato di caricamento,
    // così il tab non pianificate non resta in attesa di quello pianificate
    private data class LoadingState(val unplanned: Boolean, val planned: Boolean)

    private val transferActivityCode = homeCareActivitiesRepository.getTransferActivityCode()
    private val loadingState = MutableStateFlow(LoadingState(unplanned = false, planned = false))
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val query = MutableStateFlow<String>("")
    private val carePlanId = MutableStateFlow<Int?>(null)
    private val patientCode = MutableStateFlow<String?>(null)
    private var notPlannedActivities = MutableStateFlow<List<HomeCareUnplannedActivity>>(listOf())

    private var plannedActivities = MutableStateFlow<List<HomeCarePlannedActivity>>(listOf())

    private var transferActivity: HomeCareUnplannedActivity? = null

    // Gli id delle pianificate (id planning) e delle non pianificate (id tipo prestazione)
    // vengono da due spazi diversi e si sovrappongono: negli esempi delle API entrambi
    // partono da 1. Con le due liste su una sola schermata una selezione condivisa
    // marcherebbe la riga sbagliata, quindi i due insiemi restano separati (TS1-5).
    private val selectedPlannedIds = MutableStateFlow<List<Int>>(listOf())
    private val selectedUnplannedIds = MutableStateFlow<List<Int>>(listOf())

    var errorMessage = _errorMessage.asStateFlow()


    private val billingModes = homeCareActivitiesRepository.getBillingModes()


    private val unplannedSections = combine(
        notPlannedActivities,
        transferActivityCode,
        selectedUnplannedIds,
        billingModes,
        query
    ) { notPlannedActivities, transferActivityCode, selectedIds, billingModes, query ->
        this.transferActivity =
            notPlannedActivities.firstOrNull { it.code == transferActivityCode }

        fun toListItem(activity: HomeCareUnplannedActivity) =
            SelectCareActivityPopupUIState.ActivityListItem(
                title = activity.desc,
                code = activity.code,
                id = activity.id,
                carePlanId = null,
                isPlanned = false,
                isTransferActivity = activity.code == transferActivityCode,
                isSelected = selectedIds.contains(activity.id)
            )

        buildList {
            // La riga di trasferta resta in cima e fuori dai raggruppamenti
            transferActivity?.takeIf { it.matchesQuery(query) }?.let {
                add(SelectCareActivityPopupUIState.ActivitySection(items = listOf(toListItem(it))))
            }
            addAll(
                sectionsByBillingMode(
                    billingModes,
                    notPlannedActivities
                        .filter { it.code != transferActivityCode && it.matchesQuery(query) }
                        .map { it.idBillingMode to toListItem(it) }
                )
            )
        }
    }


    private val plannedSections = combine(
        plannedActivities,
        notPlannedActivities,
        selectedPlannedIds,
        billingModes,
        query
    ) { plannedActivities, activityTypes, selectedIds, billingModes, query ->
        // /homecare/planning non espone IDBillingMode: la modalità si ricava dal codice
        // prestazione contenuto in Type ("10601 - Preparazione dei medicamenti"),
        // cercandolo tra i tipi prestazione di /homecare/activities/types
        val billingModeIdByCode = activityTypes.associate { it.code to it.idBillingMode }
        sectionsByBillingMode(
            billingModes,
            plannedActivities
                .filter { query.isEmpty() || it.type.contains(query, true) }
                .map { activity ->
                    billingModeIdByCode[activity.type.substringBefore(" - ").trim()] to
                            SelectCareActivityPopupUIState.ActivityListItem(
                                title = activity.type,
                                code = null,
                                id = activity.id,
                                carePlanId = activity.carePlan,
                                isPlanned = true,
                                isTransferActivity = false,
                                isSelected = selectedIds.contains(activity.id)
                            )
                }
        )
    }

    private val selectionState = combine(
        plannedActivities,
        selectedPlannedIds,
        selectedUnplannedIds
    ) { plannedActivities, selectedPlanned, selectedUnplanned ->
        val plannedSelection = when {
            plannedActivities.isEmpty() || selectedPlanned.isEmpty() ->
                SelectCareActivityPopupUIState.PlannedSelection.NONE

            selectedPlanned.size >= plannedActivities.size ->
                SelectCareActivityPopupUIState.PlannedSelection.ALL

            else -> SelectCareActivityPopupUIState.PlannedSelection.SOME
        }
        plannedSelection to selectedPlanned.size + selectedUnplanned.size
    }


    val uiState = combine(
        loadingState,
        query,
        unplannedSections,
        plannedSections,
        selectionState
    ) { loadingState, query, unplannedSections, plannedSections, selectionState ->
        SelectCareActivityPopupUIState(
            isLoadingUnplanned = loadingState.unplanned,
            isLoadingPlanned = loadingState.planned,
            query = query,
            unplannedSections = unplannedSections,
            plannedSections = plannedSections,
            plannedSelection = selectionState.first,
            selectedCount = selectionState.second
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = SelectCareActivityPopupUIState(
            isLoadingUnplanned = true,
            isLoadingPlanned = true,
            query = "",
            unplannedSections = listOf(),
            plannedSections = listOf(),
            plannedSelection = SelectCareActivityPopupUIState.PlannedSelection.NONE,
            selectedCount = 0
        )
    )


    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _errorMessage.value = throwable.localizedMessage
            loadingState.value = LoadingState(unplanned = false, planned = false)
        }

    fun loadData(carePlanId: Int?, patientCode: String) {
        this.carePlanId.value = carePlanId
        this.patientCode.value = patientCode
        loadingState.value = LoadingState(unplanned = true, planned = true)
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = homeCareActivitiesRepository.getHomeCareActivitiesUnplanned(patientCode)
            // Pubblicate subito: non devono attendere le chiamate delle pianificate
            notPlannedActivities.value = res.results ?: listOf()
            _errorMessage.value = res.error?.desc
            loadingState.update { it.copy(unplanned = false) }

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
            loadingState.update { it.copy(planned = false) }
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

    /**
     * Esegue in un colpo tutto ciò che è selezionato, pianificate e non pianificate insieme.
     * Il tempo trascorso dall'ultima registrazione viene distribuito sull'intera selezione:
     * eseguirle in due passaggi separati conteggerebbe due volte lo stesso tempo, che è
     * esattamente il problema all'origine di TS1-5.
     */
    fun executeSelectedActivities(onSuccess: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val elapsedTime = userMarkingRepository.getMinutesFromLastActivityOnce() ?: return@launch
            val planned = plannedActivities.value
                .filter { selectedPlannedIds.value.contains(it.id) }
            val unplanned = notPlannedActivities.value
                .filter { selectedUnplannedIds.value.contains(it.id) }
                .sortedBy { it.code.toIntOrNull() ?: Int.MAX_VALUE }
            if (planned.isEmpty() && unplanned.isEmpty()) return@launch

            val durations = planned.map { it.duration } + unplanned.map { it.duration }
            val executionTimes = distributeTime(durations, elapsedTime)

            var lastEndTime = Date().time - (elapsedTime * 1000 * 60)
            val activitiesToSend = mutableListOf<HomeCareActivitySave>()

            planned.forEachIndexed { index, activity ->
                val executionTime = executionTimes[index]
                lastEndTime += executionTime.toLong() * 60000
                activitiesToSend.add(
                    HomeCareActivitySave(
                        idPlanning = activity.id,
                        idActivityType = null,
                        codCase = patientCode.value ?: "",
                        execDateTime = Date(lastEndTime).format("yyyy.MM.dd HH:mm"),
                        duration = executionTime,
                        notes = activity.notes,
                        showInDiary = false,
                    )
                )
            }
            unplanned.forEachIndexed { index, activity ->
                val executionTime = executionTimes[planned.size + index]
                lastEndTime += executionTime.toLong() * 60000
                activitiesToSend.add(
                    HomeCareActivitySave(
                        idActivityType = activity.id,
                        codCase = patientCode.value ?: "",
                        execDateTime = Date(lastEndTime).format("yyyy.MM.dd HH:mm"),
                        duration = executionTime,
                        notes = "",
                        showInDiary = true,
                    )
                )
            }

            activitiesToSend.sortedBy { it.execDateTime }.forEach { saveActivity(it) }
            onSuccess()
        }
    }

    suspend fun saveActivity(activity: HomeCareActivitySave) {
        val res = homeCareActivitiesRepository.addHomeCareActivity(activity)
        res.error?.desc?.let {
            _errorMessage.value = it
        }
    }

    fun changeActivitySelection(id: Int, isPlanned: Boolean, isSelected: Boolean) {
        val target = if (isPlanned) selectedPlannedIds else selectedUnplannedIds
        target.update { selected ->
            when {
                isSelected && !selected.contains(id) -> selected + id
                !isSelected -> selected - id
                else -> selected
            }
        }
    }

    /**
     * Il "seleziona tutte" vale solo per le pianificate ed è additivo: le non pianificate
     * già selezionate restano tali e vanno comunque spuntate una a una, per evitare la
     * spunta massiva che il cliente ha chiesto esplicitamente di impedire.
     */
    fun togglePlannedSelection() {
        val allPlannedIds = plannedActivities.value.map { it.id }
        selectedPlannedIds.value = if (selectedPlannedIds.value.size >= allPlannedIds.size) {
            listOf()
        } else {
            allPlannedIds
        }
    }

    fun cancelAllSection() {
        selectedPlannedIds.value = listOf()
        selectedUnplannedIds.value = listOf()
    }
}

/**
 * Raggruppa le prestazioni per Modalità di fatturazione, rispettando l'ordine con cui
 * le modalità arrivano dalla configurazione. Le prestazioni con modalità assente o
 * sconosciuta finiscono in un blocco finale, così non spariscono dalla lista.
 */
/**
 * Ripartisce i minuti trascorsi sulle prestazioni selezionate, in proporzione alla durata
 * prevista di ciascuna. Va chiamata UNA volta sull'intera selezione: applicandola due volte,
 * una per le pianificate e una per le non pianificate, lo stesso tempo verrebbe conteggiato
 * due volte (TS1-5).
 */
internal fun distributeTime(durations: List<Int>, elapsedTime: Long): List<Int> {
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

private fun HomeCareUnplannedActivity.matchesQuery(query: String) =
    query.isEmpty() || desc.contains(query, true) || code.contains(query, true)

internal fun sectionsByBillingMode(
    billingModes: List<BillingMode>,
    items: List<Pair<Int?, SelectCareActivityPopupUIState.ActivityListItem>>
): List<SelectCareActivityPopupUIState.ActivitySection> {
    val itemsByBillingModeId = items.groupBy({ it.first }, { it.second })
    val sections = billingModes.mapNotNull { billingMode ->
        itemsByBillingModeId[billingMode.id]?.let {
            SelectCareActivityPopupUIState.ActivitySection(title = billingMode.label, items = it)
        }
    }
    val ungrouped = itemsByBillingModeId
        .filterKeys { id -> billingModes.none { it.id == id } }
        .values
        .flatten()
    return if (ungrouped.isEmpty()) {
        sections
    } else {
        sections + SelectCareActivityPopupUIState.ActivitySection(
            titleRes = R.string.billing_mode_other,
            items = ungrouped
        )
    }
}
