package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Modello di dominio per il form ID PALL (IDentificazione delle situazioni di cura necessitanti cure PALLiative).
 * Rappresenta un form compilato con i dati del paziente e le risposte alle domande.
 */
@Serializable
data class IDPallForm(
    val id: String = UUID.randomUUID().toString(),
    val creationDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val patientData: PatientData = PatientData(),
    val sections: List<IDPallSection> = emptyList(),
    val compilationTimestamp: Long = System.currentTimeMillis(),
    val formType: String = "IDPALL"
)
