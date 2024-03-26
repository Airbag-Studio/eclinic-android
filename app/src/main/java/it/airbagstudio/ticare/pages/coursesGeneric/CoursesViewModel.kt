package it.airbagstudio.ticare.pages.coursesGeneric

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.CoursesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.pages.coursesGeneric.data.getLabelId
import it.airbagstudio.ticare.utils.DATE_ONLY_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class CoursesUiState(
    val courseType: ToolTag,
    val isLoading: Boolean,
    val caseName: String,
    val courseName: String,
    val date: String,
    val shift: String?,
    val coursesList: List<CourseListItem>
)

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    private val coursesRepository: CoursesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val patientCode: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    val courseTypeName: String = savedStateHandle[DestinationsArgs.COURSE_TYPE]!!
    var error by mutableStateOf<String?>(null)

    private val _uiState = MutableStateFlow<CoursesUiState>(
        CoursesUiState(
            ToolTag.PhysiotherapyCourse,
            isLoading = true, "", "", "", "",
            listOf()
        )
    )
    val uiState = _uiState.asStateFlow()

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        error = throwable.localizedMessage ?: "Generic error"
    }

    fun downloadData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val courseType = ToolTag.valueOf(courseTypeName)
            val patient = userDetailRepository.getCurrentCase()
            val shift = userDetailRepository.getCurrentShift()
            val date = userDetailRepository.getSelectedDate()?.toDate(SERVER_DATE_FORMAT) ?: Date()
            val dateParam = DateFormat.format("yyyy.MM.dd", date).toString()
            _uiState.value = CoursesUiState(
                courseType,
                true,
                patient?.getCompleteName() ?: "",
                "",
                date.format("dd/MM/yyyy"),
                shift?.publicName,
                listOf()
            )
            val courses =
                coursesRepository.getCourses(patientCode, dateParam, courseTypeName).results ?: listOf()
            val coursesListItems = courses.map {
                CourseListItem(
                    name = it.userValue,
                    time = it.dateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)?.format(
                        DATE_ONLY_TIME_FORMAT
                    ) ?: "",
                    duration = it.duration,
                    description = it.desc,
                    hasDataToUpload = false,
                    it
                )
            }
            _uiState.value = CoursesUiState(
                courseType,
                false,
                patient?.getCompleteName() ?: "",
                "",
                date.format("dd/MM/yyyy"),
                shift?.publicName,
                coursesListItems
            )
        }
    }
}