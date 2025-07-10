package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta i dati anagrafici del caregiver.
 *
 * @property name Nome del caregiver
 * @property surname Cognome del caregiver
 * @property relationship Relazione con il paziente (es. "Figlio", "Coniuge", ecc.)
 * @property contactInfo Informazioni di contatto
 */
@Serializable
data class CaregiverData(
    val name: String = "",
    val surname: String = "",
    val relationship: String = "",
    val contactInfo: String = ""
)
