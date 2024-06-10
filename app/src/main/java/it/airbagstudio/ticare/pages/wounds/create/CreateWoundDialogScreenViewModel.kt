package it.airbagstudio.ticare.pages.wounds.create

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.BodyPart
import ch.ticare.eclinic.library.entity.Wound
import ch.ticare.eclinic.library.entity.WoundImageUploadRequest
import ch.ticare.eclinic.library.entity.WoundOrigin
import ch.ticare.eclinic.library.entity.WoundPhoto
import ch.ticare.eclinic.library.entity.WoundSave
import ch.ticare.eclinic.library.entity.WoundType
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
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

data class CreateWoundDialogScreenUIState(
    val wound: NewWound,
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isValid: Boolean,
    val dropdownSelections: DropDownSelections,
    val isOnline: Boolean,
    val woundPhotos: List<WoundPhoto> = listOf(),
    val isEditingEnabled: Boolean
) {

    data class DropDownSelections(
        val selectedWoundType: WoundType?,
        val selectedWoundBodyParts: List<BodyPart>?,
        val selectedWoundOrigin: WoundOrigin?
    )

    data class NewWound(
        val date: Date,
        val woundType: String?,
        val bodyParts: String?,
        val origin: String?,
        val length: String?,
        val width: String?,
        val depth: String?,
        val description: String?,
        val images: List<Bitmap> = listOf()


    )
}

@HiltViewModel
class CreateWoundDialogScreenViewModel @Inject constructor(
    private val woundRepository: WoundRepository,
    private val userDetailRepository: UserDetailRepository,
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository
) : ViewModel() {

    lateinit var codCase: String
    var canWrite: Boolean = false

    val woundTypes = mutableStateOf<List<ListPopupItem<WoundType>>>(listOf())
    val woundBodyParts = mutableStateOf<List<ListPopupItem<BodyPart>>>(listOf())
    var woundOrigins = mutableStateOf<List<ListPopupItem<WoundOrigin>>>(listOf())

    private val date = MutableStateFlow<Date>(Date())
    private val imagesUri = MutableStateFlow<List<Bitmap>>(listOf())
    private val woundPhotos = MutableStateFlow<List<WoundPhoto>>(listOf())
    private val length = MutableStateFlow<String>("")
    private val width = MutableStateFlow<String>("")
    private val depth = MutableStateFlow<String>("")
    private val notes = MutableStateFlow<String>("")
    private val selectedWoundType = MutableStateFlow<WoundType?>(null)
    private val selectedWoundBodyParts = MutableStateFlow<List<BodyPart>?>(null)
    private val selectedWoundOrigin = MutableStateFlow<WoundOrigin?>(null)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    private var woundId: Int? = null

    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )

    var errorMessage by mutableStateOf<String?>(null)

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            errorMessage = throwable.localizedMessage
            throwable.printStackTrace()
        }

    val _size = combine(length, width, depth) { length, width, depth ->
        arrayOf(length, width, depth)
    }.catch {
        errorMessage = it.localizedMessage
    }
    val _selections = combine(
        selectedWoundType,
        selectedWoundBodyParts,
        selectedWoundOrigin
    ) { selectedWoundType, selectedWoundPositions, selectedWoundOrigin ->
        CreateWoundDialogScreenUIState.DropDownSelections(
            selectedWoundType,
            selectedWoundPositions,
            selectedWoundOrigin
        )
    }.catch {
        errorMessage = it.localizedMessage
    }

    val newWound = combine(
        imagesUri,
        date,
        _size,
        notes,
        _selections
    ) { imagesUri, date, size, notes, selections ->
        CreateWoundDialogScreenUIState.NewWound(
            date = date,
            woundType = selections.selectedWoundType?.name,
            bodyParts = selections.selectedWoundBodyParts?.map { it.name }?.joinToString(", "),
            origin = selections.selectedWoundOrigin?.name,
            length = size[0],
            width = size[1],
            depth = size[2],
            description = notes,
            images = imagesUri
        )
    }.catch {
        errorMessage = it.localizedMessage
    }

    val uiState = combine(
        newWound,
        _selections,
        isLoading,
        isSuccess,
        woundPhotos
    ) { newWound, selections, isLoading, isSuccess, woundPhoto ->
        val isValid =
            selections.selectedWoundOrigin != null &&
                    selections.selectedWoundType != null &&
                    !selections.selectedWoundBodyParts.isNullOrEmpty() &&
                    newWound.depth?.toIntOrNull() != null &&
                    newWound.length?.toIntOrNull() != null &&
                    newWound.width?.toIntOrNull() != null &&
                    newWound.description?.isNotEmpty() == true

        CreateWoundDialogScreenUIState(
            dropdownSelections = selections,
            wound = newWound,
            isLoading = isLoading,
            isSuccess = isSuccess,
            isValid = isValid,
            woundPhotos = woundPhoto,
            isOnline = offlineOnlineRepository.state.value.isOnline,
            isEditingEnabled = canWrite
        )
    }.catch {
        errorMessage = it.localizedMessage
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, CreateWoundDialogScreenUIState(
            dropdownSelections = CreateWoundDialogScreenUIState.DropDownSelections(
                null,
                null,
                null
            ),
            wound = CreateWoundDialogScreenUIState.NewWound(
                date = Date(),
                woundType = null,
                bodyParts = null,
                origin = null,
                length = null,
                width = null,
                depth = null,
                description = "",
                images = listOf()
            ),
            isLoading = false,
            isSuccess = false,
            isValid = false,
            isOnline = false,
            isEditingEnabled = canWrite
        )
    )

    fun downloadData(woundId: Int?) {
        this.woundId = woundId
        viewModelScope.launch(coroutineExceptionHandler) {
            offlineOnlineRepository.restore()
            val genderId = userDetailRepository.getCurrentCase()?.gender?.id ?: 0
            launch {
                combine(
                    woundRepository.getWoundTypes(),
                    woundRepository.getBodyParts(),
                    woundRepository.getWoundPositions()
                ) { _woundTypes, bodyParts, woundPositions ->
                    woundTypes.value = _woundTypes.map { ListPopupItem(it.name, it) }
                    woundBodyParts.value =
                        bodyParts.filter { it.idGender == genderId || it.idGender == null }
                            .map { ListPopupItem(it.name, it) }
                    woundOrigins.value = woundPositions.map { ListPopupItem(it.name, it) }
                }.collect()
            }
            delay(1000)
            launch {
                woundId?.let {
                    woundRepository.getWound(codCase, woundId).results?.firstOrNull()
                        ?.let { wound: Wound ->
                            setDate(wound.appearanceDate.toDate("dd.MM.yyyy") ?: Date())
                            setWidth(wound.width.toString())
                            setDepth(wound.depth.toString())
                            setLength(wound.length.toString())
                            setNotes(wound.appearanceDescription)
                            setWoundBodyParts(wound.parts.map {
                                BodyPart(
                                    id = it.iD,
                                    name = it.name
                                )
                            })
                            setWoundOrigin(woundOrigins.value.mapNotNull { it.item }
                                .firstOrNull { it.id == wound.iDWoundOrigin })
                            setWoundType(woundTypes.value.mapNotNull { it.item }
                                .firstOrNull { it.id == wound.iDWoundType })
                            woundPhotos.value = wound.photos
                        }
                }
            }
        }
    }

    fun setDate(date: Date) {
        this.date.value = date
    }

    fun setWidth(value: String) {
        this.width.value = value
    }

    fun setDepth(value: String) {
        this.depth.value = value
    }

    fun setLength(value: String) {
        this.length.value = value
    }

    fun setNotes(value: String) {
        this.notes.value = value
    }

    fun setWoundBodyParts(parts: List<BodyPart>?) {
        this.selectedWoundBodyParts.value = parts
    }

    fun setWoundOrigin(origin: WoundOrigin?) {
        this.selectedWoundOrigin.value = origin
    }

    fun setWoundType(woundType: WoundType?) {
        this.selectedWoundType.value = woundType
    }

    fun addImages(uriList: List<Bitmap>) {
        imagesUri.value = imagesUri.value.plus(uriList)
    }

    fun removeImage(uri: Bitmap) {
        imagesUri.value = imagesUri.value.minus(uri)
    }

    fun saveWound() {
        woundId?.let {
            patchWound(it)
        } ?: run {
            postWound()
        }

    }

    private fun patchWound(woundId: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {

            val bodyPartsId = selectedWoundBodyParts.value?.map { it.id } ?: listOf()
            val woundSave = WoundSave(
                id = woundId,
                appearanceDate = date.value.format("yyyy.MM.dd"),
                appearanceDescription = notes.value,
                bodyParts = bodyPartsId,
                cODCase = codCase ?: "",
                depth = depth.value.toInt(),
                iDWoundOrigin = selectedWoundOrigin.value!!.id,
                iDWoundType = selectedWoundType.value!!.id,
                length = length.value.toInt(),
                width = width.value.toInt()
            )

            val res = woundRepository.updateWound(woundSave)
            errorMessage = res.error?.desc
            res.results?.firstOrNull()?.id?.let { lastCreatedId ->
                imagesUri.value.forEach { bitmap ->
                    val request = WoundImageUploadRequest(
                        desc = "",
                        type = "AP",
                        woundId = lastCreatedId,
                        name = "${UUID.randomUUID()}.jpeg",
                        ecImage = bitmap.toByteArray()
                    )
                    woundRepository.uploadImage(codCase, request)
                }
                isSuccess.value = true
            } ?: run {
                isSuccess.value = false
            }
        }
    }

    private fun postWound() {
        viewModelScope.launch(coroutineExceptionHandler) {

            val bodyPartsId = selectedWoundBodyParts.value?.map { it.id } ?: listOf()
            val woundSave = WoundSave(
                appearanceDate = date.value.format("yyyy.MM.dd"),
                appearanceDescription = notes.value,
                bodyParts = bodyPartsId,
                cODCase = codCase,
                depth = depth.value.toInt(),
                iDWoundOrigin = selectedWoundOrigin.value!!.id,
                iDWoundType = selectedWoundType.value!!.id,
                length = length.value.toInt(),
                width = width.value.toInt()
            )

            val res = woundRepository.addWound(woundSave)
            errorMessage = res.error?.desc
            val isSaved = res.status?.equals("success", true) == true
            if (isSaved) {
                res.results?.firstOrNull()?.id?.let { lastCreatedId ->
                    imagesUri.value.forEach { bitmap ->
                        val request = WoundImageUploadRequest(
                            desc = "",
                            type = "AP",
                            woundId = lastCreatedId,
                            name = "${UUID.randomUUID()}.jpeg",
                            ecImage = bitmap.toByteArray()
                        )
                        woundRepository.uploadImage(codCase, request)
                    }
                }
            }
            isSuccess.value = isSaved

        }
    }


    fun clearData() {
        selectedWoundType.value = null
        selectedWoundOrigin.value = null
        selectedWoundBodyParts.value = null
        width.value = ""
        depth.value = ""
        length.value = ""
        date.value = Date()
        notes.value = ""
        isSuccess.value = false
        isLoading.value = false
        imagesUri.value = listOf()
    }
}