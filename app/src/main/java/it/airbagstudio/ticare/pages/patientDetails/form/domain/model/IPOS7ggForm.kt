package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class IPOS7ggForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(), // Reused from existing models
    val sections: List<IPOS7ggSection> = emptyList(), // Changed to IPOS7ggSection
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "IPOS7GG" // Specific type for this form, changed
)
