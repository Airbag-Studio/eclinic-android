package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a compiled COMID form.
 *
 * @property id Unique identifier of the form.
 * @property creationDate Creation date (timestamp).
 * @property lastModified Last modification date (timestamp).
 * @property patientData Patient data.
 * @property sections Form sections with responses.
 * @property compilationTimestamp Timestamp of form compilation.
 * @property formType Type of form (e.g., "CBI", "COMID").
 */
@Serializable
data class COMIDForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val sections: List<COMIDSection> = emptyList(), // Will be COMIDSection
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "COMID"
)
