package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Rappresenta un form CBI compilato.
 *
 * @property id Identificativo univoco del form
 * @property creationDate Data di creazione (timestamp)
 * @property lastModified Data di ultima modifica (timestamp)
 * @property patientData Dati del paziente
 * @property caregiverData Dati del caregiver
 * @property sections Sezioni del form con le risposte
 * @property totalScore Punteggio totale del form
 * @property compilationTimestamp Timestamp della compilazione del form
 * @property formType Tipo di form (es. "CBI", "COMID")
 */
@Serializable
data class CBIForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val caregiverData: CaregiverData = CaregiverData(),
    val sections: List<CBISection> = emptyList(),
    val totalScore: Int = 0,
    val compilationTimestamp: Long = System.currentTimeMillis(), // New single field
    val formType: String = "CBI"
)
