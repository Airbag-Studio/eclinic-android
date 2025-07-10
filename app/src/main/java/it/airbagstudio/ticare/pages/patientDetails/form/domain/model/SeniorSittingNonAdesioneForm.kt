package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SeniorSittingNonAdesioneForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val sections: List<SeniorSittingNonAdesioneSection> = emptyList(),
    val formType: String = "SENIOR_SITTING_NON_ADESIONE"
)
