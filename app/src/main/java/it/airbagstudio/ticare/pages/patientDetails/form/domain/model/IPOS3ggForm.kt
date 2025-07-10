package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class IPOS3ggForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(), // Reused from existing models
    val sections: List<IPOS3ggSection> = emptyList(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "IPOS3gg" // Specific type for this form
)
