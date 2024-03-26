package it.airbagstudio.ticare.pages.coursesGeneric.createEdit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.coursesGeneric.data.getType
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


data class CourseCreateEditScreenUIState(
    val course: CourseUIState,
    val categories: List<HomeCareCourseCategory> = listOf(),
    val isLoading: Boolean = false
){
    data class CourseUIState(
        val showInDiary: Boolean = true,
        val dateAndTime: Date = Date(),
        val duration: String = "",
        val description: String = "",
        val selectedCategory: HomeCareCourseCategory? = null
    )
}


@HiltViewModel
class CourseCreateEditScreenViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository
): ViewModel() {

    lateinit var patientCode: String
    var editingCourse: HomeCareCourse? = null
    lateinit var courseType: ToolTag


    private val showInDiary = MutableStateFlow(true)
    private val dateAndTime = MutableStateFlow(Date())
    private val duration = MutableStateFlow<Int?>(null)
    private val description = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<Int?>(null)

    private val categories = MutableStateFlow<List<HomeCareCourseCategory>>(listOf())
    private val isLoading = MutableStateFlow(false)

    var errorMessage = mutableStateOf<String?>(null)
    var isSuccess = mutableStateOf(false)

    private val coroutineExceptionHandler  = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        errorMessage.value = throwable.localizedMessage
        isLoading.value = false
    }

    private val selectedCategory = combine(selectedCategoryId,categories){ selectedCategoryId,categories ->
        categories.firstOrNull { it.id == selectedCategoryId }
    }

    private val course = combine(selectedCategory,dateAndTime,duration,description,showInDiary){ selectedCategory,dateAndTime,duration,description,showInDiary ->
        CourseCreateEditScreenUIState.CourseUIState(
            showInDiary = showInDiary,
            dateAndTime = dateAndTime,
            duration = if (duration != null) "$duration" else "",
            description = description,
            selectedCategory = selectedCategory
        )
    }

    val uiState = combine(course,categories,isLoading){ course,categories,isLoading ->
        CourseCreateEditScreenUIState(course,categories,isLoading)
    }.stateIn(viewModelScope, SharingStarted.Eagerly,
        CourseCreateEditScreenUIState(CourseCreateEditScreenUIState.CourseUIState())
    )


    fun setCategory(category: HomeCareCourseCategory){
        selectedCategoryId.value = category.id
    }

    fun setDateAndTime(date: Date){
        dateAndTime.value = date
    }

    fun setDuration(duration: Int?){
        this.duration.value = duration
    }

    fun setDescription(value: String){
        this.description.value = value
    }

    fun setShowInDiary(value: Boolean){
        this.showInDiary.value = value
    }

    fun saveCourse(){
        viewModelScope.launch {
            isLoading.value = true
            editingCourse?.let { oldCourse ->
                val newCourse = EditHomeCareCourse(
                    id = oldCourse.id,
                    caseCode = patientCode,
                    dateTime = dateAndTime.value.format("yyyy.MM.dd HH:mm"),
                    idCourseCategoryType = selectedCategoryId.value ?: 0,
                    desc = description.value,
                    duration = duration.value ?: 0,
                    showInDiary = showInDiary.value
                )

                val res = coursesRepository.updateCourse(newCourse,courseType.name)
                if (res.status == "success") {
                    isSuccess.value = true
                } else if (res.status == "error") {
                    errorMessage.value = res.error?.desc ?: ""
                }
                isLoading.value = false

            } ?: run{
                viewModelScope.launch(coroutineExceptionHandler) {
                    val newCourse = AddHomeCareCourse(
                        caseCode = patientCode,
                        dateTime = dateAndTime.value.format("yyyy.MM.dd HH:mm"),
                        idCourseCategoryType = selectedCategoryId.value ?: 0,
                        desc = description.value,
                        duration = duration.value ?: 0,
                        showInDiary = showInDiary.value
                    )

                    val res = coursesRepository.addCourse(newCourse,courseType.name)

                    if (res.status == "success") {
                        isSuccess.value = true
                    } else if (res.status == "error") {
                        errorMessage.value = res.error?.desc ?: ""
                    }
                    isLoading.value = false

                }
            }

        }
    }

    fun downloadData(){
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = coursesRepository.getCourseCategories(courseType.getType())
            errorMessage.value = res.error?.desc
            categories.value = res.results ?: listOf()

        }
    }

    fun setCourse(course: HomeCareCourse){
        editingCourse = course
        setShowInDiary(course.showInDiary)
        setDuration(course.duration)
        setDescription(course.desc)
        selectedCategoryId.value = course.categoryID
        setDateAndTime(course.dateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA) ?: Date())
    }

    fun clearState() {
        duration.value = 0
        description.value = ""
        selectedCategoryId.value = -1
        dateAndTime.value = Date()
        showInDiary.value = true
        isSuccess.value = false
        isLoading.value = false
    }

}