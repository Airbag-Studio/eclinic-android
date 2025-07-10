package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Unified IPOS form model that supports both 3gg and 7gg time periods
 */
@Serializable
data class IPOSForm(
    val id: String = "",
    val patientData: PatientData = PatientData(),
    val timePeriod: IPOSTimePeriod = IPOSTimePeriod.DAYS_3,
    val sections: List<IPOSSection> = emptyList(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

@Serializable
data class IPOSSection(
    val sectionId: String,
    val title: String,
    val questions: List<QuestionResponse>
)