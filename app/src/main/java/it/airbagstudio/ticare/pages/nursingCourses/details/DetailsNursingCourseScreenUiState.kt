package it.airbagstudio.ticare.pages.nursingCourses.details

import android.graphics.Bitmap
import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import ch.ticare.eclinic.library.entity.HomeCareCourseImage
import java.util.Date

data class DetailsNursingCourseScreenUiState(

    val newNursingCourse: NewNursingCourse = NewNursingCourse(null, Date(), null, null, false),
    val categoriesTypes: List<HomeCareCourseCategory> = listOf(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean,
    val isEditingEnabled: Boolean,
    ) {
        data class NewNursingCourse constructor(
            val courseCategoryType: HomeCareCourseCategory?,
            val dateTime: Date,
            val duration: Int?,
            val description: String?,
            val showInDiary: Boolean,
            val images: List<Bitmap> = listOf(),
            val  photos: List<HomeCareCourseImage> = listOf()
        )
}