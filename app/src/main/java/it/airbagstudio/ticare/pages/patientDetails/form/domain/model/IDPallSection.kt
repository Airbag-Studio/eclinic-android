package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Modello di dominio per una sezione del form ID PALL.
 * Rappresenta un gruppo di domande correlate all'interno del form.
 */
@Serializable
data class IDPallSection(
    val title: String,
    val order: Int,
    val questions: List<QuestionResponse> = emptyList()
)
