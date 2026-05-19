package it.airbagstudio.ticare.pages.coursesGeneric.createEdit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


data class CourseCreateEditScreenUIState(
    val course: CourseUIState,
    val categories: List<HomeCareCourseCategory> = listOf(),
    val isLoading: Boolean = false,
    val isEditingEnabled: Boolean
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

    var canWrite = false
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

    private val _showOverrideDescriptionAlert = MutableStateFlow(false)
    var showOverrideDescriptionAlert = _showOverrideDescriptionAlert.asStateFlow()

    private var isDescriptionChangedByUser = false

    var errorMessage = mutableStateOf<String?>(null)
    var isSuccess = mutableStateOf(false)

    private val coroutineExceptionHandler  = CoroutineExceptionHandler { _, throwable ->
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
        CourseCreateEditScreenUIState(course,categories,isLoading, canWrite)
    }.stateIn(viewModelScope, SharingStarted.Eagerly,
        CourseCreateEditScreenUIState(CourseCreateEditScreenUIState.CourseUIState(), isEditingEnabled = canWrite)
    )


    fun setCategory(category: HomeCareCourseCategory){
        selectedCategoryId.value = category.id
        duration.value = category.duration
        setDescriptionFromCategory(category)
    }

    fun setDateAndTime(date: Date){
        dateAndTime.value = date
    }

    fun setDuration(duration: Int?){
        this.duration.value = duration
    }

    fun setDescription(value: String){
        this.description.value = value
        isDescriptionChangedByUser = true
    }

    fun setShowInDiary(value: Boolean){
        this.showInDiary.value = value
    }

    private fun setDescriptionFromCategory(category: HomeCareCourseCategory) {
        category.defaultDescription?.let { catDescription ->
            if (catDescription.isNotEmpty()) {
                if (isDescriptionChangedByUser) {
                    _showOverrideDescriptionAlert.value = true
                    return
                }
                description.value = catDescription
            }
        }
    }

    fun keepUserDescription() {
        _showOverrideDescriptionAlert.value = false
    }

    fun overrideUserDescription() {
        _showOverrideDescriptionAlert.value = false
        isDescriptionChangedByUser = false
        viewModelScope.launch {

            selectedCategory.first()?.let { selectedCategory ->
                setDescriptionFromCategory(selectedCategory)
            }
        }

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
            val res = coursesRepository.getCourseCategories(patientCode,courseType)
            errorMessage.value = res.error?.desc
            categories.value = res.results ?: listOf()
            if (selectedCategoryId.value == null && categories.value.isNotEmpty()){
                selectedCategoryId.value = categories.value.firstOrNull { it.useAsDefault }?.id
                if (description.value.isEmpty()) {
                    description.value =
                        categories.value.firstOrNull { it.useAsDefault }?.defaultDescription ?: ""
                }
            }

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
        duration.value = null
        description.value = ""
        selectedCategoryId.value = null
        dateAndTime.value = Date()
        showInDiary.value = true
        isSuccess.value = false
        isLoading.value = false
    }

}