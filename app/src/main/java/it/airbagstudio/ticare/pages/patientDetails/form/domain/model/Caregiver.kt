package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable // If it needs to be persisted or passed via navigation args
data class Caregiver(
    val id: String = UUID.randomUUID().toString(), // Useful for list keys and identification
    val firstName: String,
    val lastName: String,
    val relationship: String,
    val contact: String
)
