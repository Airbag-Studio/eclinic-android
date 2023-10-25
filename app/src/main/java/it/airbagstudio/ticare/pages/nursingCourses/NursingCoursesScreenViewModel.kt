package it.airbagstudio.ticare.pages.nursingCourses

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.includeTime
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NursingCoursesScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val nursingCourseRepository: NursingCourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val dateTime: String = savedStateHandle[DestinationsArgs.DATE_TIME]!!
    private val shiftStart: String? = savedStateHandle[DestinationsArgs.SHIFT_START]
    private val shiftEnd: String? = savedStateHandle[DestinationsArgs.SHIFT_END]
    val shiftName: String = savedStateHandle[DestinationsArgs.SHIFT_NAME]!!

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<HomeCareCourse>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)

    var date by mutableStateOf<Date?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        date = Date(dateTime.toLong())
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            patient = userDetailRepository.getCase(patientCod).results?.firstOrNull()
            downloadTasks()
            isLoading = false
        }
    }

    private suspend fun downloadTasks(){
        var shift : OperatingShift? = null
        if (shiftStart != null && shiftEnd != null){
            shift = OperatingShift(name = shiftName, publicName = shiftName, publicShortName = shiftName, shortName = shiftName, startTime = shiftStart, stopTime = shiftEnd)
        }
        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()
        tasks = nursingCourseRepository.getNursingCourses(patientCod, date = dateParam).results ?: emptyList()
    }

    fun reloadTasks(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks()
            isLoading = false
        }
    }

    fun executeAll(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks()
            isLoading = false
        }
    }

}