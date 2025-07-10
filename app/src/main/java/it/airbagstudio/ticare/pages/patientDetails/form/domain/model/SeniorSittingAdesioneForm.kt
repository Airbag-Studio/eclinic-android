package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SeniorSittingAdesioneForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(), // Includes the new 'zone' field
    val sections: List<SeniorSittingAdesioneSection> = emptyList(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "SeniorSittingAdesione"
)
