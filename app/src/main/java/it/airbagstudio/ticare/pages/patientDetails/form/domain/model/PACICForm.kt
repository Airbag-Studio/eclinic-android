package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import java.util.UUID

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PACICForm (
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val caregiverData: CaregiverData = CaregiverData(),
    val questions: List<QuestionResponse> = emptyList(),
    val totalScore: Int = 0,
    val compilationTimestamp: Long = System.currentTimeMillis(), // New single field
    val formType: String = "PACIC-S"
)