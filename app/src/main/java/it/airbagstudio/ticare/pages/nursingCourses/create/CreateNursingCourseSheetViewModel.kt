package it.airbagstudio.ticare.pages.nursingCourses.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CreateNursingCourseSheetViewModel @Inject constructor(
    private val nursingCourseRepository: NursingCourseRepository,
) : ViewModel() {

    private val selectedCategoryId = MutableStateFlow<Int?>(null)
    private var listOfCategories: MutableStateFlow<List<HomeCareCourseCategory>?> = MutableStateFlow<List<HomeCareCourseCategory>?>(emptyList())
    private val selectedDate = MutableStateFlow<Date>(Date())
    private val description = MutableStateFlow("")
    private val duration = MutableStateFlow<Int?>(null)
    private val showInDiary = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading.value = false
        errorMessage.value = throwable.localizedMessage
    }

    private val selectedCategory = combine(listOfCategories, selectedCategoryId) { categories, _id ->
        categories?.firstOrNull { it.id == _id } ?: categories?.firstOrNull { it.useAsDefault }
    }.map {
        duration.value = it?.duration
        it
    }

    private val newCourses = combine(
        selectedCategory,
        selectedDate,
        description,
        duration,
        showInDiary
    ) { category, date, description, duration, showInDiary ->
        CreateNursingCourseScreenUiState.NewNursingCourse(category, date, duration, description, showInDiary)
    }

    val uiState = combine(
        newCourses,
        listOfCategories,
        isLoading,
        isSuccess,
        errorMessage
    ) { newService, categories, isLoading, isSuccess, errorMessage ->
        CreateNursingCourseScreenUiState(
            newService,
            categories ?: emptyList(),
            isLoading,
            isSuccess,
            errorMessage
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreateNursingCourseScreenUiState(
            newNursingCourse = CreateNursingCourseScreenUiState.NewNursingCourse(null, Date(), null, null, false)
        )
    )

    init {
        loadCategory()
    }

    private fun loadCategory() {
        viewModelScope.launch {
            listOfCategories.value = nursingCourseRepository.getNursingCourseCategory().results.also {
                selectedCategoryId.value = it?.find { cat -> cat.useAsDefault}?.id
            }
        }
    }

    fun setDate(date: Date) {
        selectedDate.value = date
    }

    fun setDescription(value: String) {
        description.value = value
    }

    fun setDuration(value: Int?) {
        duration.value = value
    }


    fun setCategoryId(categoryId: Int?) {
        selectedCategoryId.value =  categoryId
    }

    fun clearState() {
        errorMessage.value = null
        isSuccess.value = false
    }


    fun saveNursingCourse(patientCode: String) {
        isLoading.value = true
        viewModelScope.launch {
            val newCourse = AddHomeCareCourse(
                caseCode = patientCode,
                dateTime = selectedDate.value.format("yyyy-MM-dd HH:mm:00"),
                idCourseCategoryType = selectedCategoryId.value ?: 0,
                desc = description.value,
                duration = duration.value ?: 0,
                showInDiary = showInDiary.value
            )

            val res = nursingCourseRepository.addNursingCourse(newCourse)
            if (res.status == "success") {
                isSuccess.value = true
            } else if (res.status == "error") {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false

        }
    }
}