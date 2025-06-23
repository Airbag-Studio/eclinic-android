package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CAMSection(
    val title: String,
    val order: Int,
    val questions: List<QuestionResponse> = emptyList()
)
