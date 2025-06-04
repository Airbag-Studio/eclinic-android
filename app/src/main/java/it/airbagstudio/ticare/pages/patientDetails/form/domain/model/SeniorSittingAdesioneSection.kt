package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SeniorSittingAdesioneSection(
    val sectionId: String, // e.g., "scaled_questions", "open_questions"
    val title: String, // User-visible title for the section
    val questions: List<QuestionResponse> = emptyList() // Reusing QuestionResponse
)
