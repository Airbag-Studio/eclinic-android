package it.airbagstudio.ticare.pages.wounds.create

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.BodyPart
import ch.ticare.eclinic.library.entity.WoundOrigin
import ch.ticare.eclinic.library.entity.WoundPart
import ch.ticare.eclinic.library.entity.WoundType
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

data class CreateWoundDialogScreenUIState(
    val wound: NewWound,
    val dropdownSelections:DropDownSelections,
) {

    data class DropDownSelections(
        val selectedWoundType : WoundType?,
        val selectedWoundPositions: List<BodyPart>?,
        val selectedWoundOrigin: WoundOrigin?
    )
    data class NewWound(
        val date: Date,
        val woundType: String?,
        val position: String?,
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
    private val woundRepository: WoundRepository

) : ViewModel() {

    val woundTypes = woundRepository.getWoundTypes()
    val woundPositions = woundRepository.getBodyParts()
    val woundOrigins = woundRepository.getWoundPositions()

    private val date = MutableStateFlow<Date>(Date())
    private val imagesUri = MutableStateFlow<List<Bitmap>>(listOf())
    private val length = MutableStateFlow<String>("")
    private val width = MutableStateFlow<String>("")
    private val depth = MutableStateFlow<String>("")
    private val notes = MutableStateFlow<String>("")
    private val selectedWoundType = MutableStateFlow<WoundType?>(null)
    private val selectedWoundPositions = MutableStateFlow<List<BodyPart>?>(null)
    private val selectedWoundOrigin = MutableStateFlow<WoundOrigin?>(null)

    val _size = combine(length, width, depth) { length, width, depth ->
        arrayOf(length, width, depth)
    }
    val _selections = combine(selectedWoundType,selectedWoundPositions,selectedWoundOrigin){ selectedWoundType,selectedWoundPositions,selectedWoundOrigin ->
        CreateWoundDialogScreenUIState.DropDownSelections(
            selectedWoundType,
            selectedWoundPositions,
            selectedWoundOrigin
        )
    }


    val uiState = combine(imagesUri, date, _size, notes,_selections) { imagesUri, date, size, notes,selections ->
        CreateWoundDialogScreenUIState(
            dropdownSelections = selections,
            wound = CreateWoundDialogScreenUIState.NewWound(
                date = date,
                woundType = selections.selectedWoundType?.name,
                position = selections.selectedWoundPositions?.map { it.name }?.joinToString(", "),
                origin = selections.selectedWoundOrigin?.name,
                length = size[0],
                width = size[1],
                depth = size[2],
                description = notes,
                images = imagesUri
            )
        )
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, CreateWoundDialogScreenUIState(
            dropdownSelections = CreateWoundDialogScreenUIState.DropDownSelections(null,null,null),
            wound =  CreateWoundDialogScreenUIState.NewWound(
                date = Date(),
                woundType = null,
                position = null,
                origin = null,
                length = null,
                width = null,
                depth = null,
                description = "",
                images = listOf()
            )
        )
    )


    fun setDate(date: Date) {
        this.date.value = date
    }

    fun setWidth(value: String){
        this.width.value = value
    }

    fun setDepth(value: String){
        this.depth.value = value
    }

    fun setLength(value: String){
        this.length.value = value
    }

    fun setNotes(value: String){
        this.notes.value = value
    }

    fun setWoundPositions(parts: List<BodyPart>?){
        this.selectedWoundPositions.value = parts
    }

    fun setWoundOrigin(origin: WoundOrigin?){
        this.selectedWoundOrigin.value = origin
    }

    fun setWoundType(woundType: WoundType?){
        this.selectedWoundType.value = woundType
    }

    fun addImages(uriList: List<Bitmap>) {
        imagesUri.value = imagesUri.value.plus(uriList)
    }

    fun removeImage(uri: Bitmap) {
        imagesUri.value = imagesUri.value.minus(uri)
    }


}