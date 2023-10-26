package it.airbagstudio.ticare.pages.nursingCourses.create

import ch.ticare.eclinic.library.entity.HomeCareCourseCategory
import java.util.Date

data class CreateNursingCourseScreenUiState(

    val newNursingCourse: NewNursingCourse = NewNursingCourse(null, Date(), null, null, false),
    val categoriesTypes: List<HomeCareCourseCategory> = listOf(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
    ) {
        data class NewNursingCourse constructor(
            val courseCategoryType: HomeCareCourseCategory?,
            val dateTime: Date,
            val duration: Int?,
            val description: String?,
            val showInDiary: Boolean
        )
}