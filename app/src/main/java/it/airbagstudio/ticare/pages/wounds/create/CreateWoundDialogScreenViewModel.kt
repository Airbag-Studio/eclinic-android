package it.airbagstudio.ticare.pages.wounds.create

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

data class CreateWoundDialogScreenUIState(
    val wound: NewWound

) {
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
class CreateWoundDialogScreenViewModel @Inject constructor() : ViewModel() {


    private val date = MutableStateFlow<Date>(Date())
    private val imagesUri = MutableStateFlow<List<Bitmap>>(listOf())
    private val length = MutableStateFlow<String>("")
    private val width = MutableStateFlow<String>("")
    private val depth = MutableStateFlow<String>("")
    private val notes = MutableStateFlow<String>("")

    val _size = combine(length, width, depth) { length, width, depth ->
        arrayOf(length, width, depth)

    }

    val uiState = combine(imagesUri, date, _size, notes) { imagesUri, date, size, notes ->
        CreateWoundDialogScreenUIState(
            CreateWoundDialogScreenUIState.NewWound(
                date = date,
                woundType = null,
                position = null,
                origin = null,
                length = size[0],
                width = size[1],
                depth = size[2],
                description = notes,
                images = imagesUri
            )
        )
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, CreateWoundDialogScreenUIState(
            CreateWoundDialogScreenUIState.NewWound(
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

    fun addImages(uriList: List<Bitmap>) {
        imagesUri.value = imagesUri.value.plus(uriList)
    }

    fun removeImage(uri: Bitmap) {
        imagesUri.value = imagesUri.value.minus(uri)
    }


}