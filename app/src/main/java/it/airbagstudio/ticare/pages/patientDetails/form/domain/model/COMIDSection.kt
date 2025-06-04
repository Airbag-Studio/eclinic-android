package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a section of the COMID form.
 *
 * @property title The title of the section.
 * @property order The display order of the section.
 * @property questions List of question responses in the section.
 */
@Serializable
data class COMIDSection(
    val title: String,
    val order: Int,
    val questions: List<QuestionResponse> = emptyList()
)
