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
import ch.ticare.eclinic.library.entity.OfflineSection
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.CoursesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.toDate
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@HiltViewModel
class NursingCoursesScreenViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val coursesRepository: CoursesRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val patientCode: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val courseTypeName: String = savedStateHandle[DestinationsArgs.COURSE_TYPE]!!

    var isLoading by mutableStateOf(false)
    var tasks by mutableStateOf<List<HomeCareCourse>>(listOf())
    var errorMessage by mutableStateOf<String?>(null)
    var patient by mutableStateOf<CaseDetail?>(null)
    var shiftName by mutableStateOf<String?>(null)

    var date by mutableStateOf<Date?>(null)
    var modifiedIds by mutableStateOf<List<String>>(listOf())

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading = false
        errorMessage = throwable.localizedMessage
    }

    init {
        date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
        shiftName = userDetailRepository.getCurrentShift()?.name
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            patient = userDetailRepository.getCase(patientCode).results?.firstOrNull()
            downloadTasks()
            isLoading = false
        }
    }

    private suspend fun downloadTasks(){
        modifiedIds = offlineOnlineRepository.getModifiedIdForSection(patientCode,OfflineSection.NursingCourse)
        val dateParam =  DateFormat.format("yyyy.MM.dd", date).toString()
        tasks = coursesRepository.getCourses(patientCode, date = dateParam, courseTypeName).results ?: emptyList()
    }

    fun reloadTasks(){
        viewModelScope.launch(coroutineExceptionHandler) {
            isLoading = true
            downloadTasks()
            isLoading = false
        }
    }

}