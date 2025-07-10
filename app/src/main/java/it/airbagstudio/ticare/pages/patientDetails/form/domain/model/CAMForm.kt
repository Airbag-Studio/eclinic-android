package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CAMForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "CAM",
    val sections: List<CAMSection> = emptyList()
)
