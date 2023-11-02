package it.airbagstudio.ticare.pages.carePlans.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCareActivitySave
import ch.ticare.eclinic.library.entity.HomeCareUnplannedActivity
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class CreateEditCareScreenUiState(
    val isSuccess: Boolean,
    val title: String,
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
class CreateEditCareScreenViewModel @Inject constructor(
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
) : ViewModel() {


    private val isSuccess = MutableStateFlow<Boolean>(false)

    private val carePlanId = MutableStateFlow<Int?>(null)
    private val activityId = MutableStateFlow<Int?>(null)
    private val codCase = MutableStateFlow<String?>(null)
    private val plannedActivityId = MutableStateFlow<Int?>(null)
    private val idActivityType = MutableStateFlow<Int?>(null)
    private val date = MutableStateFlow<Date>(Date())
    private val duration = MutableStateFlow<Int>(0)
    private val notes = MutableStateFlow<String>("")
    private val showInDiary = MutableStateFlow<Boolean>(false)

    private val isLoading = MutableStateFlow<Boolean>(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val plannedActivity = combine(codCase,plannedActivityId,carePlanId){ codCase, plannedActivityId, carePlanId ->
        if (codCase != null && plannedActivityId != null && carePlanId != null) {
            val activity = homeCareActivitiesRepository.getHomeCareActivitiesPlanned(codCase, carePlanId).results?.firstOrNull { it.id == plannedActivityId }
            setDuration(activity?.duration ?: 0)
            activity
        }else{
            null
        }
    }

    private val notPlannedActivity: Flow<HomeCareUnplannedActivity?> = combine(codCase,idActivityType){ codCase, idActivityType ->
        if (codCase != null && idActivityType != null){
            val activity = homeCareActivitiesRepository.getHomeCareActivitiesUnplanned().results?.firstOrNull { it.id == idActivityType }
            setDuration(activity?.duration ?: 0)
            activity
        }else{
            null
        }

    }

    private val care =
        combine(date, duration, notes, showInDiary) { date, duration, notes, showInDiary ->
            CreateEditCareScreenUiState.Item(date, duration, notes, showInDiary)
        }

    private val title = combine(plannedActivity,notPlannedActivity){ plannedActivity,notPlannedActivity ->
        plannedActivity?.type ?: notPlannedActivity?.desc ?: ""
    }

    val uiState = combine(care, isLoading, errorMessage,title,isSuccess) { care, isLoading, errorMessage,title,isSuccess ->
        CreateEditCareScreenUiState(
            title = title,
            errorMessage = errorMessage,
            isLoading = isLoading,
            item = care,
            isSuccess = isSuccess
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = CreateEditCareScreenUiState(
            title = "",
            errorMessage = null,
            isLoading = false,
            item = CreateEditCareScreenUiState.Item(Date(), 0, "", false),
            isSuccess = false
        )
    )

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage.value = throwable.localizedMessage
        isLoading.value = false
    }

    fun setCarePlanId(id: Int?){
        this.carePlanId.value = id
    }

    fun setActivityId(id: Int?){
        this.activityId.value = id
    }

    fun setCodCase(cod: String){
        this.codCase.value = cod
    }

    fun setPlannedActivityId(id: Int?){
        this.plannedActivityId.value = id
    }

    fun setIdActivityType(id: Int?){
        this.idActivityType.value = id
    }

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
        this.isSuccess.value = false
        this.idActivityType.value = null
        this.plannedActivityId.value = null
        this.activityId.value = null
        date.value = Date()
        duration.value = 0
        notes.value = ""
        showInDiary.value = false
    }



    fun saveCare(){
        if (codCase.value != null){
            viewModelScope.launch(coroutineExceptionHandler) {
                isLoading.value = true
                if (activityId.value != null){
                    val item = HomeCareActivitySave(
                        codCase = codCase.value!!,
                        execDateTime = date.value.format("yyyy.MM.dd HH.mm"),
                        duration = duration.value,
                        notes = notes.value,
                        showInDiary = showInDiary.value,
                        id = activityId.value
                    )
                    val res = homeCareActivitiesRepository.editHomeCareActivity(item)
                    res.error?.desc?.let {
                        errorMessage.value = it
                    } ?: run{
                        isSuccess.value = true
                    }
                }else{
                    val item = HomeCareActivitySave(
                        codCase = codCase.value!!,
                        idActivityType = idActivityType.value,
                        execDateTime = date.value.format("yyyy.MM.dd HH.mm"),
                        duration = duration.value,
                        notes = notes.value,
                        showInDiary = showInDiary.value,
                        idPlanning = plannedActivityId.value
                    )
                    val res = homeCareActivitiesRepository.addHomeCareActivity(item)
                    res.error?.desc?.let {
                        errorMessage.value = it
                    } ?: run{
                        isSuccess.value = true
                    }
                }
                isLoading.value = false

            }
        }

    }
}