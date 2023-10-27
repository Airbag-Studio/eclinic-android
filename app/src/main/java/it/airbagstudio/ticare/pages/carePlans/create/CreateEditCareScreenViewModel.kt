package it.airbagstudio.ticare.pages.carePlans.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.lang.Error
import java.util.Date
import javax.inject.Inject

data class CreateEditCareScreenUiState(
    val title: String,
    val description: String,
    val errorMessage: String?,
    val isLoading: Boolean,
    val item: Item

) {
    data class Item(
        val date: Date,
        val duration: Int,
        val notes: String,
        val showInDiary: Boolean
    )
}

@HiltViewModel
class CreateEditCareScreenViewModel @Inject constructor() : ViewModel() {

    private var date = MutableStateFlow<Date>(Date())
    private var duration = MutableStateFlow<Int>(0)
    private var notes = MutableStateFlow<String>("")
    private val showInDiary = MutableStateFlow<Boolean>(false)

    private val isLoading = MutableStateFlow<Boolean>(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val care =
        combine(date, duration, notes, showInDiary) { date, duration, notes, showInDiary ->
            CreateEditCareScreenUiState.Item(date, duration, notes, showInDiary)
        }

    val uiState = combine(care, isLoading, errorMessage) { care, isLoading, errorMessage ->
        CreateEditCareScreenUiState(
            title = "Igiene e Comfort",
            description = "Igiene completa a letto",
            errorMessage = errorMessage,
            isLoading = isLoading,
            item = care
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = CreateEditCareScreenUiState(
            title = "Igiene e Comfort",
            description = "Igiene completa a letto",
            errorMessage = null,
            isLoading = false,
            item = CreateEditCareScreenUiState.Item(Date(), 0, "", false)
        )
    )


    fun setDate(date: Date){
        this.date.value = date
    }

    fun setNotes(value:String){
        this.notes.value = value
    }

    fun setDuration(value: Int){
        this.duration.value = value
    }

    fun setShowInDiary(value: Boolean){
        this.showInDiary.value = value
    }

    fun clearErrors() {
        errorMessage.value = null
    }

    fun clearData() {
        date.value = Date()
        duration.value = 0
        notes.value = ""
        showInDiary.value = false
    }
}