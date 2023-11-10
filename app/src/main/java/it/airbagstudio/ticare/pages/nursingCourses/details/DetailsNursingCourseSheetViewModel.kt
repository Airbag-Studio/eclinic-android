package it.airbagstudio.ticare.pages.nursingCourses.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EditNursingCourseSheetViewModel @Inject constructor(
    private val nursingCourseRepository: NursingCourseRepository,
) : ViewModel() {

    private val selectedCategoryStateFlow = MutableStateFlow<HomeCareCourseCategory?>(null)
    private val selectedCategoryId = MutableStateFlow<Int?>(null)
    private var listOfCategories: MutableStateFlow<List<HomeCareCourseCategory>?> = MutableStateFlow(emptyList())
    private val selectedDate = MutableStateFlow(Date())
    private val description = MutableStateFlow("")
    private val duration = MutableStateFlow<Int?>(null)
    private val showInDiary = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private var screenType = mutableStateOf<ScreenType>(ScreenType.Add)
    private var editNursingCourseId: Int = 0

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        isLoading.value = false
        errorMessage.value = throwable.localizedMessage
    }

    private val selectedCategory = combine(listOfCategories, selectedCategoryId) { categories, id ->
        categories?.firstOrNull { it.id == id } ?: categories?.firstOrNull { it.useAsDefault }
    }.map {
        selectedCategoryStateFlow.value = it
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
        DetailsNursingCourseScreenUiState.NewNursingCourse(category, date, duration, description, showInDiary)
    }

    val uiState = combine(
        newCourses,
        listOfCategories,
        isLoading,
        isSuccess,
        errorMessage
    ) { newService, categories, isLoading, isSuccess, errorMessage ->
        DetailsNursingCourseScreenUiState(
            newService,
            categories ?: emptyList(),
            isLoading,
            isSuccess,
            errorMessage
        )

    }.catch {
        errorMessage.value = it.localizedMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsNursingCourseScreenUiState(
            newNursingCourse = DetailsNursingCourseScreenUiState.NewNursingCourse(selectedCategoryStateFlow.value, selectedDate.value, duration.value, description.value, showInDiary.value)
        )
    )

    fun loadCategory() {
        viewModelScope.launch(coroutineExceptionHandler) {
            listOfCategories.value = nursingCourseRepository.getNursingCourseCategory().results.also {
                if(screenType.value == ScreenType.Add) {
                selectedCategoryId.value = it?.find { cat -> cat.useAsDefault }?.id
                }
            }
        }
    }

    fun setScreenType(type: ScreenType) {
        screenType.value = type
        when(type) {
            ScreenType.Add -> setDefaultParams()
            is ScreenType.Edit -> setPreviousCategory(type.homeCareCourse)
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

    fun setShowInDiary(show: Boolean) {
        showInDiary.value = show
    }

    fun getShowInDiary(): Boolean = showInDiary.value

    fun clearState() {
        errorMessage.value = null
        isSuccess.value = false
    }


    fun saveButtonClick(patientCode: String) {
        when (screenType.value) {
            is ScreenType.Edit -> editNursingCourse(patientCode)
            ScreenType.Add -> addNursingCourse(patientCode)
        }
    }

    private fun setPreviousCategory(actualCourse: HomeCareCourse) {
        editNursingCourseId = actualCourse.id
        selectedCategoryId.value = actualCourse.categoryID
        duration.value = actualCourse.duration
        selectedDate.value = actualCourse.dateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA) ?: Date()
        description.value = actualCourse.desc
        showInDiary.value = actualCourse.showInDiary
    }

    private fun setDefaultParams() {
        editNursingCourseId = 0
        selectedCategoryId.value = null
        duration.value = null
        selectedDate.value =  Date()
        description.value = ""
        showInDiary.value = false
    }

    private fun addNursingCourse(patientCode: String) {
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val newCourse = AddHomeCareCourse(
                caseCode = patientCode,
                dateTime = selectedDate.value.format("yyyy.MM.dd HH.mm"),
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

    private fun editNursingCourse(patientCode: String) {
        isLoading.value = true
        viewModelScope.launch(coroutineExceptionHandler) {
            val newCourse = EditHomeCareCourse(
                id = editNursingCourseId,
                caseCode = patientCode,
                dateTime = selectedDate.value.format("yyyy.MM.dd HH.mm"),
                idCourseCategoryType = selectedCategoryId.value ?: 0,
                desc = description.value,
                duration = duration.value ?: 0,
                showInDiary = showInDiary.value
            )

            val res = nursingCourseRepository.updateNursingCourse(newCourse)
            if (res.status == "success") {
                isSuccess.value = true
            } else if (res.status == "error") {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false

        }
    }
}

sealed class ScreenType {
    data class Edit(val homeCareCourse: HomeCareCourse): ScreenType()
    object Add: ScreenType()
}