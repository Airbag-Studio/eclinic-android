package it.airbagstudio.ticare.pages.falls.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Fall
import ch.ticare.eclinic.library.entity.FallAutonomyDegrees
import ch.ticare.eclinic.library.entity.FallCauseDetail
import ch.ticare.eclinic.library.entity.FallCauses
import ch.ticare.eclinic.library.entity.FallConsequences
import ch.ticare.eclinic.library.entity.FallLocations
import ch.ticare.eclinic.library.entity.FallPreStatuses
import ch.ticare.eclinic.library.repository.FallsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.utils.FALL_DATE_FORMAT
import it.airbagstudio.ticare.utils.FALL_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class CreateEditFallDialogUiState(
    val dateTime: Date,
    val notes: String,
    val detector: String,
    val consequenceDetails: String,
    val witnesses: String,
    // dropdowns
    val autonomyDegreeLabel: String?,
    val causeLabel: String?,
    val causeDetailLabel: String?,
    val consequencesLabel: String?,
    val locationLabel: String?,
    val preStatusLabel: String?,
    // booleans
    val lighting: Boolean,
    val intervention: Boolean,
    val visualIssues: Boolean,
    val disorientation: Boolean,
    val withRestraint: Boolean,
    val nonSlipShoes: Boolean,
    val withWitnesses: Boolean,
    // ack flags (true = done, stored as current date string)
    val medicAck: Boolean,
    val familyAck: Boolean,
    val refPersonAck: Boolean,
    // status
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isValid: Boolean
)

@HiltViewModel
class CreateEditFallDialogScreenViewModel @Inject constructor(
    private val fallsRepository: FallsRepository
) : ViewModel() {

    lateinit var codCase: String

    val autonomyDegrees = mutableStateOf<List<ListPopupItem<FallAutonomyDegrees>>>(listOf())
    val causes = mutableStateOf<List<ListPopupItem<FallCauses>>>(listOf())
    val causeDetails = mutableStateOf<List<ListPopupItem<FallCauseDetail>>>(listOf())
    val consequences = mutableStateOf<List<ListPopupItem<FallConsequences>>>(listOf())
    val locations = mutableStateOf<List<ListPopupItem<FallLocations>>>(listOf())
    val preStatuses = mutableStateOf<List<ListPopupItem<FallPreStatuses>>>(listOf())
    private var allCauseDetails: List<FallCauseDetail> = listOf()

    // Core fields
    private val dateTime = MutableStateFlow<Date>(Date())
    private val notes = MutableStateFlow("")
    private val detector = MutableStateFlow("")
    private val consequenceDetails = MutableStateFlow("")
    private val witnesses = MutableStateFlow("")

    // Dropdown selections
    private val selectedAutonomyDegree = MutableStateFlow<FallAutonomyDegrees?>(null)
    private val selectedCause = MutableStateFlow<FallCauses?>(null)
    private val selectedCauseDetail = MutableStateFlow<FallCauseDetail?>(null)
    private val selectedConsequences = MutableStateFlow<FallConsequences?>(null)
    private val selectedLocation = MutableStateFlow<FallLocations?>(null)
    private val selectedPreStatus = MutableStateFlow<FallPreStatuses?>(null)

    // Boolean fields
    private val lighting = MutableStateFlow(false)
    private val intervention = MutableStateFlow(false)
    private val visualIssues = MutableStateFlow(false)
    private val disorientation = MutableStateFlow(false)
    private val withRestraint = MutableStateFlow(false)
    private val nonSlipShoes = MutableStateFlow(false)
    private val withWitnesses = MutableStateFlow(false)

    // Ack fields: stored as Boolean in UI, converted to date string on save
    // empty string = not done, non-empty = done (date stored by server)
    private val medicAckDate = MutableStateFlow("")
    private val familyAckDate = MutableStateFlow("")
    private val refPersonAckDate = MutableStateFlow("")

    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)

    var errorMessage by mutableStateOf<String?>(null)
    private var fallId: Int? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorMessage = throwable.localizedMessage
        throwable.printStackTrace()
    }

    // Intermediate: dropdown selections
    private val _selections = combine(
        selectedAutonomyDegree,
        selectedCause,
        selectedCauseDetail,
        selectedConsequences,
        selectedLocation,
        selectedPreStatus
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        Selections(
            autonomyDegree = values[0] as FallAutonomyDegrees?,
            cause = values[1] as FallCauses?,
            causeDetail = values[2] as FallCauseDetail?,
            consequences = values[3] as FallConsequences?,
            location = values[4] as FallLocations?,
            preStatus = values[5] as FallPreStatuses?
        )
    }.catch { errorMessage = it.localizedMessage }

    // Intermediate: boolean fields combined
    private val _booleans = combine(
        lighting,
        intervention,
        visualIssues,
        disorientation,
        withRestraint
    ) { lighting, intervention, visualIssues, disorientation, withRestraint ->
        Booleans1(lighting, intervention, visualIssues, disorientation, withRestraint)
    }.catch { errorMessage = it.localizedMessage }

    private val _booleans2 = combine(
        nonSlipShoes,
        withWitnesses,
        medicAckDate,
        familyAckDate,
        refPersonAckDate
    ) { nonSlipShoes, withWitnesses, medicAckDate, familyAckDate, refPersonAckDate ->
        Booleans2(nonSlipShoes, withWitnesses, medicAckDate, familyAckDate, refPersonAckDate)
    }.catch { errorMessage = it.localizedMessage }

    // Intermediate: text fields
    private val _texts = combine(
        dateTime,
        notes,
        detector,
        consequenceDetails,
        witnesses
    ) { dateTime, notes, detector, consequenceDetails, witnesses ->
        Texts(dateTime, notes, detector, consequenceDetails, witnesses)
    }.catch { errorMessage = it.localizedMessage }

    val uiState = combine(
        _selections,
        _texts,
        _booleans,
        _booleans2,
        combine(isLoading, isSuccess) { l, s -> Pair(l, s) }
    ) { selections, texts, booleans, booleans2, (isLoading, isSuccess) ->
        CreateEditFallDialogUiState(
            dateTime = texts.dateTime,
            notes = texts.notes,
            detector = texts.detector,
            consequenceDetails = texts.consequenceDetails,
            witnesses = texts.witnesses,
            autonomyDegreeLabel = selections.autonomyDegree?.name,
            causeLabel = selections.cause?.name,
            causeDetailLabel = selections.causeDetail?.name,
            consequencesLabel = selections.consequences?.name,
            locationLabel = selections.location?.name,
            preStatusLabel = selections.preStatus?.name,
            lighting = booleans.lighting,
            intervention = booleans.intervention,
            visualIssues = booleans.visualIssues,
            disorientation = booleans.disorientation,
            withRestraint = booleans.withRestraint,
            nonSlipShoes = booleans2.nonSlipShoes,
            withWitnesses = booleans2.withWitnesses,
            medicAck = booleans2.medicAckDate.isNotEmpty(),
            familyAck = booleans2.familyAckDate.isNotEmpty(),
            refPersonAck = booleans2.refPersonAckDate.isNotEmpty(),
            isLoading = isLoading,
            isSuccess = isSuccess,
            isValid = selections.autonomyDegree != null &&
                    selections.cause != null &&
                    selections.consequences != null &&
                    selections.location != null &&
                    selections.preStatus != null
        )
    }.catch {
        errorMessage = it.localizedMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CreateEditFallDialogUiState(
            dateTime = Date(),
            notes = "",
            detector = "",
            consequenceDetails = "",
            witnesses = "",
            autonomyDegreeLabel = null,
            causeLabel = null,
            causeDetailLabel = null,
            consequencesLabel = null,
            locationLabel = null,
            preStatusLabel = null,
            lighting = false,
            intervention = false,
            visualIssues = false,
            disorientation = false,
            withRestraint = false,
            nonSlipShoes = false,
            withWitnesses = false,
            medicAck = false,
            familyAck = false,
            refPersonAck = false,
            isLoading = false,
            isSuccess = false,
            isValid = false
        )
    )

    fun downloadData(fall: Fall? = null) {
        this.fallId = fall?.id
        viewModelScope.launch(coroutineExceptionHandler) {
            val degrees = fallsRepository.getFallAutonomyDegrees().first()
            val causeList = fallsRepository.getFallCauses().first()
            val causeDetailList = fallsRepository.getFallCauseDetails().first()
            val consequenceList = fallsRepository.getFallConsequences().first()
            val locationList = fallsRepository.getFallLocations().first()
            val preStatusList = fallsRepository.getFallPreStatuses().first()

            autonomyDegrees.value = withNone(degrees.map { ListPopupItem(it.name, it) })
            causes.value = withNone(causeList.map { ListPopupItem(it.name, it) })
            allCauseDetails = causeDetailList
            causeDetails.value = withNone(causeDetailList.map { ListPopupItem(it.name, it) })
            consequences.value = withNone(consequenceList.map { ListPopupItem(it.name, it) })
            locations.value = withNone(locationList.map { ListPopupItem(it.name, it) })
            preStatuses.value = withNone(preStatusList.map { ListPopupItem(it.name, it) })

            fall?.let { populateFrom(it) }
        }
    }

    private fun populateFrom(fall: Fall) {
        dateTime.value = fall.dateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA) ?: Date()
        notes.value = fall.notes
        detector.value = fall.detector
        consequenceDetails.value = fall.consequenceDetails
        witnesses.value = fall.witnesses
        lighting.value = fall.lighting
        intervention.value = fall.intervention
        visualIssues.value = fall.visualIssues
        disorientation.value = fall.disorientation
        withRestraint.value = fall.withRestraint
        nonSlipShoes.value = fall.nonSlipShoes
        withWitnesses.value = fall.withWitnesses
        medicAckDate.value = fall.medicAckDate
        familyAckDate.value = fall.familyAckDate
        refPersonAckDate.value = fall.refPersonAckDate
        selectedAutonomyDegree.value = autonomyDegrees.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idAutonomyDegree }
        selectedCause.value = causes.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idCause }
        selectedCauseDetail.value = causeDetails.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idCauseDetail }
        selectedConsequences.value = consequences.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idConsequences }
        selectedLocation.value = locations.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idLocation }
        selectedPreStatus.value = preStatuses.value.mapNotNull { it.item }
            .firstOrNull { it.id == fall.idPreStatus }
    }

    // Setters
    fun setDateTime(date: Date) { dateTime.value = date }
    fun setNotes(value: String) { notes.value = value }
    fun setDetector(value: String) { detector.value = value }
    fun setConsequenceDetails(value: String) { consequenceDetails.value = value }
    fun setWitnesses(value: String) { witnesses.value = value }
    fun setAutonomyDegree(value: FallAutonomyDegrees?) { selectedAutonomyDegree.value = value }
    fun setCause(value: FallCauses?) {
        selectedCause.value = value
        selectedCauseDetail.value = null
        causeDetails.value = withNone(allCauseDetails
            .filter { value == null || it.iDCause == value.id }
            .map { ListPopupItem(it.name, it) })
    }
    fun setCauseDetail(value: FallCauseDetail?) { selectedCauseDetail.value = value }
    fun setConsequences(value: FallConsequences?) { selectedConsequences.value = value }
    fun setLocation(value: FallLocations?) { selectedLocation.value = value }
    fun setPreStatus(value: FallPreStatuses?) { selectedPreStatus.value = value }
    fun setLighting(value: Boolean) { lighting.value = value }
    fun setIntervention(value: Boolean) { intervention.value = value }
    fun setVisualIssues(value: Boolean) { visualIssues.value = value }
    fun setDisorientation(value: Boolean) { disorientation.value = value }
    fun setWithRestraint(value: Boolean) { withRestraint.value = value }
    fun setNonSlipShoes(value: Boolean) { nonSlipShoes.value = value }
    fun setWithWitnesses(value: Boolean) { withWitnesses.value = value }
    fun setMedicAck(checked: Boolean) {
        medicAckDate.value = if (checked) Date().format(FALL_DATE_FORMAT) else ""
    }
    fun setFamilyAck(checked: Boolean) {
        familyAckDate.value = if (checked) Date().format(FALL_DATE_FORMAT) else ""
    }
    fun setRefPersonAck(checked: Boolean) {
        refPersonAckDate.value = if (checked) Date().format(FALL_DATE_FORMAT) else ""
    }

    fun saveFall() {
        fallId?.let { editFall(it) } ?: addFall()
    }

    private fun buildFall(id: Int?) = Fall(
        /* case              */ codCase,
        /* consequenceDetails*/ consequenceDetails.value,
        /* dateTime          */ dateTime.value.format(FALL_DATE_TIME_FORMAT),
        /* detector          */ detector.value,
        /* disorientation    */ disorientation.value,
        /* familyAckDate     */ familyAckDate.value,
        /* id                */ id,
        /* idAutonomyDegree  */ selectedAutonomyDegree.value!!.id,
        /* idCause           */ selectedCause.value!!.id,
        /* idCauseDetail     */ selectedCauseDetail.value?.id,
        /* idConsequences    */ selectedConsequences.value!!.id,
        /* idLocation        */ selectedLocation.value!!.id,
        /* idPreStatus       */ selectedPreStatus.value!!.id,
        /* intervention      */ intervention.value,
        /* lighting          */ lighting.value,
        /* medicAckDate      */ medicAckDate.value,
        /* nonSlipShoes      */ nonSlipShoes.value,
        /* notes             */ notes.value,
        /* refPersonAckDate  */ refPersonAckDate.value,
        /* user              */ null,
        /* visualIssues      */ visualIssues.value,
        /* withRestraint     */ withRestraint.value,
        /* withWitnesses     */ withWitnesses.value,
        /* witnesses         */ witnesses.value
    )

    private fun addFall() {
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading.value = true
            val res = fallsRepository.addFall(buildFall(null))
            errorMessage = res.error?.desc
            isSuccess.value = res.status?.equals("success", true) == true
            isLoading.value = false
        }
    }

    private fun editFall(id: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading.value = true
            val res = fallsRepository.editFall(buildFall(id))
            errorMessage = res.error?.desc
            isSuccess.value = res.results?.isNotEmpty() == true || res.status?.equals("success", true) == true
            isLoading.value = false
        }
    }

    fun clearData() {
        dateTime.value = Date()
        notes.value = ""
        detector.value = ""
        consequenceDetails.value = ""
        witnesses.value = ""
        selectedAutonomyDegree.value = null
        selectedCause.value = null
        selectedCauseDetail.value = null
        selectedConsequences.value = null
        selectedLocation.value = null
        selectedPreStatus.value = null
        lighting.value = false
        intervention.value = false
        visualIssues.value = false
        disorientation.value = false
        withRestraint.value = false
        nonSlipShoes.value = false
        withWitnesses.value = false
        medicAckDate.value = ""
        familyAckDate.value = ""
        refPersonAckDate.value = ""
        isLoading.value = false
        isSuccess.value = false
        fallId = null
    }

    private data class Selections(
        val autonomyDegree: FallAutonomyDegrees?,
        val cause: FallCauses?,
        val causeDetail: FallCauseDetail?,
        val consequences: FallConsequences?,
        val location: FallLocations?,
        val preStatus: FallPreStatuses?
    )

    private data class Texts(
        val dateTime: Date,
        val notes: String,
        val detector: String,
        val consequenceDetails: String,
        val witnesses: String
    )

    private data class Booleans1(
        val lighting: Boolean,
        val intervention: Boolean,
        val visualIssues: Boolean,
        val disorientation: Boolean,
        val withRestraint: Boolean
    )

    private data class Booleans2(
        val nonSlipShoes: Boolean,
        val withWitnesses: Boolean,
        val medicAckDate: String,
        val familyAckDate: String,
        val refPersonAckDate: String
    )

    private fun <T> withNone(items: List<ListPopupItem<T>>): List<ListPopupItem<T>> =
        mutableListOf(ListPopupItem("--", null as T?)).also { it.addAll(items) }
}
