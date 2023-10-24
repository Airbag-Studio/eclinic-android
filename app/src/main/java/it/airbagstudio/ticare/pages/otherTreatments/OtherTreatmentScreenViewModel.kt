package it.airbagstudio.ticare.pages.otherTreatments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.OtherService
import ch.ticare.eclinic.library.repository.OtherServiceRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getItemDesc
import it.airbagstudio.ticare.utils.getNumber
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class OtherServicesUIState(
    val services: List<OtherTreatmentItem> = listOf(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val selectedDate: Date? = null,
    val selectedShift: OperatingShift? = null,
    val patientName: String = ""
)

@HiltViewModel
class OtherTreatmentScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val otherServiceRepository: OtherServiceRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {



    val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val services = MutableStateFlow<List<OtherService>>(listOf())
    private val throwable = MutableStateFlow<Throwable?>(null)
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val _selectedDate = MutableStateFlow<Date?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, _throwable ->
        throwable.value = _throwable
        isLoading.value = false
    }

    var selectedService by mutableStateOf<OtherService?>(null)


    val uiState: StateFlow<OtherServicesUIState> = combine(_selectedDate,services,throwable,isLoading){date,services,throwable,isLoading ->
        val case = userDetailRepository.getCurrentCase()
        OtherServicesUIState(
            services = services.map { OtherTreatmentItem(
                name = it.itemGroup,
                description = it.getItemDesc(),
                number = it.getNumber(),
                id = it.id

            ) },
            errorMessage = throwable?.localizedMessage,
            isLoading = isLoading,
            selectedDate = date,
            selectedShift = userDetailRepository.getCurrentShift(),
            patientName = "${case?.surname ?: ""} ${case?.name ?: ""}"
        )
    }.stateIn(
        scope = viewModelScope,
        started =  SharingStarted.WhileSubscribed(5000),
        initialValue = OtherServicesUIState(
            isLoading = true
        )
    )

    init {
        downloadData()
    }


    fun downloadData(){
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val selectedDate = if (userDetailRepository.getSelectedDate() != null){
                userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
            } else{
                Date()
            }
            _selectedDate.value = selectedDate
            val startCalendar = Calendar.getInstance()
            startCalendar.time = selectedDate
            val endCalendar = Calendar.getInstance()
            endCalendar.time = selectedDate
            userDetailRepository.getCurrentShift()?.let { shift ->
                val start = LocalTime.parse(shift.startTime)
                val end = LocalTime.parse(shift.stopTime)
                startCalendar.set(Calendar.HOUR,start.hour)
                startCalendar.set(Calendar.MINUTE,start.minute)
                endCalendar.set(Calendar.HOUR,end.hour)
                endCalendar.set(Calendar.MINUTE,end.minute)
                if (start.isAfter(end)){
                    endCalendar.add(Calendar.HOUR,24)
                }
            } ?: run {
                startCalendar.set(Calendar.HOUR,0)
                startCalendar.set(Calendar.MINUTE,0)
                endCalendar.set(Calendar.HOUR,0)
                endCalendar.set(Calendar.MINUTE,0)
                endCalendar.add(Calendar.HOUR,24)
            }
            services.value = otherServiceRepository.getOtherServices(patientCod,startCalendar.time.format(
                SERVER_PARAMETER_DATE_TIME_FORMAT),endCalendar.time.format(SERVER_PARAMETER_DATE_TIME_FORMAT)).results ?: listOf()
            isLoading.value = false
        }
    }

    fun setSelectedServiceId(id: Int){
        selectedService = services.value.firstOrNull { it.id == id }
    }
}