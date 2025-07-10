package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SeniorSittingNonAdesioneSection(
    val title: String,
    val order: Int,
    val introductoryText: String? = null, // For Section 1
    val questions: List<QuestionResponse> = emptyList()
)
