package it.airbagstudio.ticare.pages.nursingCourses.details

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.AddHomeCareCourse
import ch.ticare.eclinic.library.entity.EditHomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.HomeCareCourseImage
import ch.ticare.eclinic.library.entity.HomeCareCourseImageRequest
import ch.ticare.eclinic.library.entity.ToolTag
import ch.ticare.eclinic.library.entity.WoundImageUploadRequest
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.CoursesRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.di.AuthRepositoryImpl
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toByteArray
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
import java.util.UUID

@HiltViewModel
class EditNursingCourseSheetViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val offlineOnlineRepository: OfflineOnlineRepository,
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    var canWrite = false
    var isOnline by mutableStateOf(false)
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
    private val imagesUri = MutableStateFlow<List<Bitmap>>(listOf())
    private val homeCareCourseImages = MutableStateFlow<List<HomeCareCourseImage>>(listOf())

    private var courseTypeName = ""

    var requestImageRequestData: ImageRequestData = ImageRequestData(
        authRepository.getBaseURL(),
        authRepository.getToken() ?: ""
    )

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
        showInDiary,
        imagesUri,
        homeCareCourseImages
    ) { items ->
      val category = items[0] as HomeCareCourseCategory?
        val date = items[1] as Date
        val description = items[2] as String
        val duration = items[3] as Int?
        val showInDiary = items[4] as Boolean
        val imagesUri = items[5] as List<Bitmap>
        val homeCareCourseImages = items[6] as List<HomeCareCourseImage>
        DetailsNursingCourseScreenUiState.NewNursingCourse(category, date, duration, description, showInDiary,imagesUri,homeCareCourseImages)
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
            errorMessage,
            screenType.value is ScreenType.Edit,
            canWrite
        )

    }.catch {
        errorMessage.value = it.localizedMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsNursingCourseScreenUiState(
            newNursingCourse = DetailsNursingCourseScreenUiState.NewNursingCourse(selectedCategoryStateFlow.value, selectedDate.value, duration.value, description.value, showInDiary.value),
            isEditing = false,
            isEditingEnabled = canWrite
        )
    )

    fun loadCategory(patientCode: String) {
        if (courseTypeName.isEmpty()) return
        viewModelScope.launch(coroutineExceptionHandler) {
            listOfCategories.value = coursesRepository.getCourseCategories(patientCode,ToolTag.valueOf(courseTypeName)).results.also {
                if(screenType.value == ScreenType.Add) {
                selectedCategoryId.value = it?.find { cat -> cat.useAsDefault }?.id
                }
            }
        }
    }

    fun setScreenType(type: ScreenType) {
        isOnline = offlineOnlineRepository.state.value.isOnline
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

    fun clearError(){
        errorMessage.value = null
    }

    fun clearState() {
        isSuccess.value = false
        imagesUri.value = listOf()
    }


    fun saveButtonClick(patientCode: String) {
        when (screenType.value) {
            is ScreenType.Edit -> editNursingCourse(patientCode)
            ScreenType.Add -> addNursingCourse(patientCode)
        }
    }

    fun addImages(uriList: List<Bitmap>) {
        imagesUri.value = imagesUri.value.plus(uriList)
    }

    fun removeImage(uri: Bitmap) {
        imagesUri.value = imagesUri.value.minus(uri)
    }


    private fun setPreviousCategory(actualCourse: HomeCareCourse) {
        homeCareCourseImages.value = actualCourse.photos
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
                dateTime = selectedDate.value.format("yyyy.MM.dd HH:mm"),
                idCourseCategoryType = selectedCategoryId.value ?: 0,
                desc = description.value,
                duration = duration.value ?: 0,
                showInDiary = showInDiary.value
            )

            val res = coursesRepository.addCourse(newCourse, courseTypeName)

            if (res.status == "success") {
                res.results?.firstOrNull()?.id?.let { lastCreatedId ->
                    imagesUri.value.forEach { bitmap ->
                        val request = HomeCareCourseImageRequest(
                            courseId = lastCreatedId,
                            name = "${UUID.randomUUID()}.jpeg",
                            ecImage = bitmap.toByteArray(),
                            t = courseTypeName
                        )
                        coursesRepository.uploadImage(patientCode,request)
                    }
                }
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
                dateTime = selectedDate.value.format("yyyy.MM.dd HH:mm"),
                idCourseCategoryType = selectedCategoryId.value ?: 0,
                desc = description.value,
                duration = duration.value ?: 0,
                showInDiary = showInDiary.value
            )

            val res = coursesRepository.updateCourse(newCourse, courseTypeName)
            if (res.status == "success") {
                isSuccess.value = true
            } else if (res.status == "error") {
                errorMessage.value = res.error?.desc ?: ""
            }
            isLoading.value = false

        }
    }

    fun setCourseTypeName(courseTypeName: String) {
        this.courseTypeName = courseTypeName
    }

    fun getCourseTypeName(): String = courseTypeName
}

sealed class ScreenType {
    data class Edit(val homeCareCourse: HomeCareCourse): ScreenType()
    object Add: ScreenType()
}