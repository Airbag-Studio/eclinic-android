package it.airbagstudio.ticare.pages.wounds.checks.create

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddWoundCheck
import ch.ticare.eclinic.library.entity.PainIntensite
import ch.ticare.eclinic.library.entity.PainType
import ch.ticare.eclinic.library.entity.WoundArea
import ch.ticare.eclinic.library.entity.WoundDepth
import ch.ticare.eclinic.library.entity.WoundFibrin
import ch.ticare.eclinic.library.entity.WoundGranulation
import ch.ticare.eclinic.library.entity.WoundImageUploadRequest
import ch.ticare.eclinic.library.entity.WoundNecrosis
import ch.ticare.eclinic.library.entity.WoundPhoto
import ch.ticare.eclinic.library.entity.WoundSecretion
import ch.ticare.eclinic.library.entity.WoundSmell
import ch.ticare.eclinic.library.entity.WoundSurroundingSkin
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toByteArray
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class CheckCreateDialogScreenUIState(
    val check: NewCheck,
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isValid: Boolean,
    val isOnline: Boolean,
    val dropDownSelections1: DropDownSelections1,
    val dropDownSelections2: DropDownSelections2
) {

    data class DropDownSelections1(
        val selectedWoundArea: WoundArea?,
        val selectedWoundDepth: WoundDepth?,
        val selectedWoundNecrosis: WoundNecrosis?,
        val selectedWoundFibrin: WoundFibrin?,
        val selectedWoundGranulation: WoundGranulation?,
    )

    data class DropDownSelections2(
        val selectedWoundSmell: WoundSmell?,
        val selectedWoundSecretion: WoundSecretion?,
        val selectedWoundSurroundingSkin: WoundSurroundingSkin?,
        val selectedWoundPainType: PainType?,
        val selectedWoundPainIntensite: PainIntensite?,
    )

    data class NewCheck(
        val date: Date,
        val woundArea: String?,
        val woundDepth: String?,
        val woundNecrosis: String?,
        val woundFibrin: String?,
        val woundGranulation: String?,
        val woundSmell: String?,
        val woundSecretion: String?,
        val woundSurroundingSkin: String?,
        val woundPainType: String?,
        val woundPainIntensite: String?,
        val medicationType: String?,
        val images: List<Bitmap> = listOf()
    )

}

@HiltViewModel
class CheckCreateDialogScreenViewModel @Inject constructor(
    private val woundRepository: WoundRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
): ViewModel() {

    lateinit var codCase: String
    var idWound: Int? = null
    var idGender: Int? = null
    private var checkId: Int? = null

    val woundArea = mutableStateOf<List<ListPopupItem<WoundArea>>>(listOf())
    val woundDepth = mutableStateOf<List<ListPopupItem<WoundDepth>>>(listOf())
    val woundNecrosis = mutableStateOf<List<ListPopupItem<WoundNecrosis>>>(listOf())
    val woundFibrin = mutableStateOf<List<ListPopupItem<WoundFibrin>>>(listOf())
    val woundGranulation = mutableStateOf<List<ListPopupItem<WoundGranulation>>>(listOf())
    val woundSmell = mutableStateOf<List<ListPopupItem<WoundSmell>>>(listOf())
    val woundSecretion = mutableStateOf<List<ListPopupItem<WoundSecretion>>>(listOf())
    val woundSurroundingSkin = mutableStateOf<List<ListPopupItem<WoundSurroundingSkin>>>(listOf())
    val woundPainType = mutableStateOf<List<ListPopupItem<PainType>>>(listOf())
    val woundPainIntensite = mutableStateOf<List<ListPopupItem<PainIntensite>>>(listOf())
    val checkImages = mutableStateOf<List<WoundPhoto>>(listOf())

    private val date = MutableStateFlow<Date>(Date())
    private val imagesUri = MutableStateFlow<List<Bitmap>>(listOf())
    private val medicationType = MutableStateFlow<String>("")
    private val selectedWoundArea = MutableStateFlow<WoundArea?>(null)
    private val selectedWoundDepth = MutableStateFlow<WoundDepth?>(null)
    private val selectedWoundNecrosis = MutableStateFlow<WoundNecrosis?>(null)
    private val selectedWoundFibrin = MutableStateFlow<WoundFibrin?>(null)
    private val selectedWoundGranulation = MutableStateFlow<WoundGranulation?>(null)
    private val selectedWoundSmell = MutableStateFlow<WoundSmell?>(null)
    private val selectedWoundSecretion = MutableStateFlow<WoundSecretion?>(null)
    private val selectedWoundSurroundingSkin = MutableStateFlow<WoundSurroundingSkin?>(null)
    private val selectedWoundPainType = MutableStateFlow<PainType?>(null)
    private val selectedWoundPainIntensite = MutableStateFlow<PainIntensite?>(null)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)


    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )


    var errorMessage by mutableStateOf<String?>(null)

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            errorMessage = throwable.localizedMessage
        }

    val _selections1 = combine(
        selectedWoundArea,
        selectedWoundDepth,
        selectedWoundNecrosis,
        selectedWoundFibrin,
        selectedWoundGranulation
    ) { selectedWoundArea,
        selectedWoundDepth,
        selectedWoundNecrosis,
        selectedWoundFibrin,
        selectedWoundGranulation ->
        CheckCreateDialogScreenUIState.DropDownSelections1(
            selectedWoundArea,
            selectedWoundDepth,
            selectedWoundNecrosis,
            selectedWoundFibrin,
            selectedWoundGranulation
        )
    }

    val _selections2 = combine(
        selectedWoundSmell,
        selectedWoundSecretion,
        selectedWoundSurroundingSkin,
        selectedWoundPainType,
        selectedWoundPainIntensite
    ) { selectedWoundSmell,
        selectedWoundSecretion,
        selectedWoundSurroundingSkin,
        selectedWoundPainType,
        selectedWoundPainIntensite ->
        CheckCreateDialogScreenUIState.DropDownSelections2(
            selectedWoundSmell,
            selectedWoundSecretion,
            selectedWoundSurroundingSkin,
            selectedWoundPainType,
            selectedWoundPainIntensite
        )
    }

    val _stateLoading = combine(
        isLoading,
        isSuccess
    ) { isLoading, isSuccess ->
        arrayOf(isLoading, isSuccess)
    }.catch {
        errorMessage = it.localizedMessage
    }

    val newCheck = combine(
        imagesUri,
        date,
        medicationType,
        _selections1,
        _selections2
    ) { imagesUri, date, medicationType, selections1, selections2 ->
        CheckCreateDialogScreenUIState.NewCheck(
            date,
            selections1.selectedWoundArea?.name,
            selections1.selectedWoundDepth?.name,
            selections1.selectedWoundNecrosis?.name,
            selections1.selectedWoundFibrin?.name,
            selections1.selectedWoundGranulation?.name,
            selections2.selectedWoundSmell?.name,
            selections2.selectedWoundSecretion?.name,
            selections2.selectedWoundSurroundingSkin?.name,
            selections2.selectedWoundPainType?.name,
            selections2.selectedWoundPainIntensite?.name,
            medicationType,
            images = imagesUri
        )
    }

    val uiState = combine(
        newCheck,
        _selections1,
        _selections2,
        medicationType,
        _stateLoading
    ) { newCheck, selections1, selections2, medicationType, stateLoading ->
        val isValid =
            selections1.selectedWoundArea != null &&
                    selections1.selectedWoundDepth != null &&
                    selections1.selectedWoundNecrosis != null &&
                    selections1.selectedWoundFibrin != null &&
                    selections1.selectedWoundGranulation != null &&
                    selections2.selectedWoundSmell != null &&
                    selections2.selectedWoundSecretion != null &&
                    selections2.selectedWoundSurroundingSkin != null &&
                    selections2.selectedWoundPainType != null &&
                    selections2.selectedWoundPainIntensite != null
        CheckCreateDialogScreenUIState(
            dropDownSelections1 = selections1,
            dropDownSelections2 = selections2,
            check = newCheck,
            isLoading = stateLoading[0],
            isSuccess = stateLoading[1],
            isValid = isValid,
            isOnline = offlineOnlineRepository.state.value.isOnline
        )
    }.catch {
        errorMessage = it.localizedMessage
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, CheckCreateDialogScreenUIState(
            dropDownSelections1 = CheckCreateDialogScreenUIState.DropDownSelections1(
                null,
                null,
                null,
                null,
                null
            ),
            dropDownSelections2 = CheckCreateDialogScreenUIState.DropDownSelections2(
                null,
                null,
                null,
                null,
                null
            ),
            check = CheckCreateDialogScreenUIState.NewCheck(
                date = Date(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                medicationType = "",
                images = listOf()
            ),
            isLoading = false,
            isSuccess = false,
            isValid = false,
            isOnline = false
        )
    )

    fun downloadData(checkId: Int?){
        this.checkId = checkId
        viewModelScope.launch(coroutineExceptionHandler) {
            offlineOnlineRepository.restore()
            launch {
                combine(
                    woundRepository.getWoundAreas(),
                    woundRepository.getWoundDepth(),
                    woundRepository.getWoundNecrosis(),
                    woundRepository.getWoundFibrin(),
                    woundRepository.getWoundGranulation(),
                ) { _woundAreas,
                    _woundDepth,
                    _woundNecrosis,
                    _woundFibrin,
                    _woundGranulation ->
                    woundArea.value = _woundAreas.map { ListPopupItem(it.name, it) }
                    woundDepth.value = _woundDepth.map { ListPopupItem(it.name, it) }
                    woundNecrosis.value = _woundNecrosis.map { ListPopupItem(it.name, it) }
                    woundFibrin.value = _woundFibrin.map { ListPopupItem(it.name, it) }
                    woundGranulation.value = _woundGranulation.map { ListPopupItem(it.name, it) }
                }.collect()
            }
            launch {
                combine(
                    woundRepository.getWoundSmell(),
                    woundRepository.getWoundSecretion(),
                    woundRepository.getWoundSurroundingSkin(),
                    woundRepository.getPainTypes(),
                    woundRepository.getPainIntesities(),
                ) { _woundSmell,
                    _woundSecretion,
                    _woundSurroundingSkin,
                    _woundPainType,
                    _woundPainIntensite ->
                    woundSmell.value = _woundSmell.map { ListPopupItem(it.name, it) }
                    woundSecretion.value = _woundSecretion.map { ListPopupItem(it.name, it) }
                    woundSurroundingSkin.value =
                        _woundSurroundingSkin.map { ListPopupItem(it.name, it) }
                    woundPainType.value = _woundPainType.map { ListPopupItem(it.name, it) }
                    woundPainIntensite.value =
                        _woundPainIntensite.map { ListPopupItem(it.name, it) }
                }.collect()
            }
            delay(1000)
            launch {
                val checkId = checkId ?: return@launch
                val woundId = idWound ?: return@launch
                val wound = woundRepository.getWound(codCase, woundId).results?.firstOrNull() ?: return@launch
                val check = wound.checks.firstOrNull { it.iD == checkId }
                    ?: return@launch
                setWoundArea(woundArea.value.mapNotNull { it.item }.firstOrNull { check.area.endsWith(it.name) })
                setWoundDepth(woundDepth.value.mapNotNull { it.item }.firstOrNull { check.depth.endsWith(it.name) })
                setWoundNecrosis(woundNecrosis.value.mapNotNull { it.item }.firstOrNull { check.necrosis.endsWith(it.name) })
                setWoundFibrin(woundFibrin.value.mapNotNull { it.item }.firstOrNull { check.fibrin.endsWith(it.name) })
                setWoundGranulation(woundGranulation.value.mapNotNull { it.item }.firstOrNull { check.granulationTissue.endsWith(it.name) })
                setWoundSmell(woundSmell.value.mapNotNull { it.item }.firstOrNull { check.smell.endsWith(it.name) })
                setWoundSecretion(woundSecretion.value.mapNotNull { it.item }.firstOrNull { check.secretion.endsWith(it.name) })
                setWoundSurroundingSkin(woundSurroundingSkin.value.mapNotNull { it.item }.firstOrNull { check.surroundingSkin.endsWith(it.name) })
                setWoundPainType(woundPainType.value.mapNotNull { it.item }.firstOrNull { check.pain.endsWith(it.name) })
                setWoundPainIntensite(woundPainIntensite.value.mapNotNull { it.item }.firstOrNull { check.painIntensity.endsWith(it.name) })
                setMedicationType(check.medicationType)
                setDate(check.dateTime.toDate("dd.MM.yyyy HH:mm") ?: Date())
                checkImages.value = wound.photos.filter { it.iDCheck == checkId }
            }
        }
    }

    fun setWoundArea(woundArea: WoundArea?) {
        this.selectedWoundArea.value = woundArea
    }

    fun setWoundDepth(woundDepth: WoundDepth?) {
        this.selectedWoundDepth.value = woundDepth
    }
    fun setWoundNecrosis(woundNecrosis: WoundNecrosis?) {
        this.selectedWoundNecrosis.value = woundNecrosis
    }
    fun setWoundFibrin(woundFibrin: WoundFibrin?) {
        this.selectedWoundFibrin.value = woundFibrin
    }
    fun setWoundGranulation(woundGranulation: WoundGranulation?) {
        this.selectedWoundGranulation.value = woundGranulation
    }

    fun setWoundSmell(woundSmell: WoundSmell?) {
        this.selectedWoundSmell.value = woundSmell
    }

    fun setWoundSecretion(woundSecretion: WoundSecretion?) {
        this.selectedWoundSecretion.value = woundSecretion
    }

    fun setWoundSurroundingSkin(woundSurroundingSkin: WoundSurroundingSkin?) {
        this.selectedWoundSurroundingSkin.value = woundSurroundingSkin
    }

    fun setWoundPainType(painType: PainType?) {
        this.selectedWoundPainType.value = painType
    }

    fun setWoundPainIntensite(painIntensite: PainIntensite?) {
        this.selectedWoundPainIntensite.value = painIntensite
    }

    fun setMedicationType(medicationType: String) {
        this.medicationType.value = medicationType
    }

    fun setDate(date: Date) {
        this.date.value = date
    }

    fun addImages(uriList: List<Bitmap>) {
        imagesUri.value = imagesUri.value.plus(uriList)
    }

    fun removeImage(uri: Bitmap) {
        imagesUri.value = imagesUri.value.minus(uri)
    }

    fun saveCheck() {
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading.value = true
            val woundCheck = AddWoundCheck(
                id = checkId,
                codArea = selectedWoundArea.value!!.cod,
                codDepth = selectedWoundDepth.value!!.cod.toString(),
                codNecrosis = selectedWoundNecrosis.value!!.cod,
                codFibrin = selectedWoundFibrin.value!!.cod,
                codGranulationTissue = selectedWoundGranulation.value!!.cod,
                codSmell = selectedWoundSmell.value!!.cod,
                codSecretion = selectedWoundSecretion.value!!.cod,
                codSurroundingSkin = selectedWoundSurroundingSkin.value!!.cod,
                dateTime = date.value.format("yyyy.MM.dd HH:mm"),
                iDWound = idWound ?: 0,
                iDWoundPain = selectedWoundPainType.value!!.id,
                iDWoundPainIntensity = selectedWoundPainIntensite.value!!.id,
                medicationType = medicationType.value
            )
            val res = if (checkId != null) {
                woundRepository.updateCheck(codCase,woundCheck)
            }else{
                woundRepository.addCheck(codCase,woundCheck)
            }

            errorMessage = res.error?.desc
            val isSaved = res.status?.equals("success",true) == true
            if(isSaved){
                res.results?.firstOrNull()?.id?.let { lastCreatedId ->
                    imagesUri.value.forEach { bitmap ->
                        val request = WoundImageUploadRequest(
                            desc = "",
                            type = "CK",
                            woundId = woundCheck.iDWound,
                            name = "${UUID.randomUUID()}.jpeg",
                            ecImage = bitmap.toByteArray(),
                            checkId = lastCreatedId
                        )
                        woundRepository.uploadImage(codCase,request)
                    }
                }
            }
            isSuccess.value = isSaved

            isSuccess.value = true
            isLoading.value = false
        }
    }

    fun clearData(){
        checkId = null
        selectedWoundArea.value = null
        selectedWoundDepth.value = null
        selectedWoundNecrosis.value = null
        selectedWoundFibrin.value = null
        selectedWoundGranulation.value = null
        selectedWoundSmell.value = null
        selectedWoundSecretion.value = null
        selectedWoundSurroundingSkin.value = null
        selectedWoundPainType.value = null
        selectedWoundPainIntensite.value = null
        date.value = Date()
        medicationType.value = ""
        isSuccess.value = false
        isLoading.value = false
        imagesUri.value = listOf()
    }

}