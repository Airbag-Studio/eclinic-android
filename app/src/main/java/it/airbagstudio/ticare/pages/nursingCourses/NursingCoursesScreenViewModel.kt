package it.airbagstudio.ticare.pages.nursingCourses

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@HiltViewModel
class NursingCoursesScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val nursingCourseRepository: NursingCourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val patientCode: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val dateTime: String = savedStateHandle[DestinationsArgs.DATE_TIME]!!
    val shiftName: String = savedStateHandle[DestinationsArgs.SHIFT_NAME]!!

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<HomeCareCourse>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)

    var date by mutableStateOf<Date?>(null)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        date = Date(dateTime.toLong())
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            patient = userDetailRepository.getCase(patientCode).results?.firstOrNull()
            downloadTasks()
            isLoading = false
        }
    }

    private suspend fun downloadTasks(){
        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()
        tasks = nursingCourseRepository.getNursingCourses(patientCode, date = dateParam).results ?: emptyList()
    }

    fun reloadTasks(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks()
            isLoading = false
        }
    }

}