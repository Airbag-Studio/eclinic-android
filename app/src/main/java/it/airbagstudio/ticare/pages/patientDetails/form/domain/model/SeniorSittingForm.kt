package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import ch.ticare.eclinic.library.entity.Contact
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SeniorSittingForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val selectedCaregiver: Contact? = null,
    val type: SeniorSittingType = SeniorSittingType.ADESIONE,
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val sections: List<SeniorSittingSection> = emptyList(),
    val formType: String = type.typeName
)

@Serializable
data class SeniorSittingSection(
    val sectionId: String,
    val title: String,
    val order: Int = 0,
    val introductoryText: String? = null,
    val questions: List<QuestionResponse> = emptyList()
)