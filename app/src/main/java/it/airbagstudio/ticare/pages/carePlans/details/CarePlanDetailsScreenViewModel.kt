package it.airbagstudio.ticare.pages.carePlans.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.HomeCarePlan
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        val content: String
    )
}

@HiltViewModel
class CarePlanDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
) : ViewModel() {

    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val planId: String = savedStateHandle[DestinationsArgs.ID]!!

    private val isLoading = MutableStateFlow(false)
    private val plans = MutableStateFlow<List<HomeCarePlan>>(listOf())
    private val errorMessage = MutableStateFlow<String?>(null)

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            isLoading.value = false
            errorMessage.value = throwable.localizedMessage
        }



    val uiState = combine(isLoading, errorMessage, plans) { isLoading, errorMessage, plans ->
        val selectedPan = planId.toIntOrNull()?.let { id ->
            plans.firstOrNull { it.id == id }
        }
        val activities = homeCareActivitiesRepository.getHomeCareActivities(patientCod).results?.map {
            CarePlanCoursesListItem(
                title = it.type,
                executed = true,
                id = it.id,
                planned = it.isScheduled,
                activity = it
            )
        }
        val textItems = listOf(
            CarePlanDetailsUIState.TextItems(R.string.diagnosis, selectedPan?.diagnosis ?: ""),
            CarePlanDetailsUIState.TextItems(R.string.problem, selectedPan?.problemDescription ?: ""),
            CarePlanDetailsUIState.TextItems(R.string.defining_features, selectedPan?.definingFeatures?.map { it.name }?.joinToString("\n") ?: ""),
            CarePlanDetailsUIState.TextItems(R.string.related_factors, selectedPan?.relatedFactors?.map { it.name }?.joinToString("\n") ?: ""),
            CarePlanDetailsUIState.TextItems(R.string.goal, selectedPan?.goal ?: ""),

            )


        CarePlanDetailsUIState(
            isLoading = isLoading,
            errorMessage = errorMessage,
            title = selectedPan?.title ?: "",
            date = selectedPan?.openDate?.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: "",
            cares = activities ?: listOf(),
            textItem = textItems
        )


    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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

    fun downloadData() {
        clearError()
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = homeCareActivitiesRepository.getHomeCarePlans(patientCod)
            if (res.status == "success") {
                plans.value = res.results ?: listOf()
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