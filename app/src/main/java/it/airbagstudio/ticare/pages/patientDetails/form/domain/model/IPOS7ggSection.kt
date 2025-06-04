package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IPOS7ggSection(
    val sectionId: String, // To identify the section, e.g., "Q1", "Q2", "Q3_Q9", "Q10"
    val title: String, // User-visible title for the section
    val questions: List<QuestionResponse> = emptyList() // Reusing QuestionResponse
)
