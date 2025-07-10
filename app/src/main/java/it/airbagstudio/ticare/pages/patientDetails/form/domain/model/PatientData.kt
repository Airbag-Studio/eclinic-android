package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta i dati anagrafici del paziente.
 *
 * @property name Nome del paziente
 * @property surname Cognome del paziente
 * @property birthDate Data di nascita (timestamp)
 * @property notes Note aggiuntive sul paziente
 */
@Serializable
data class PatientData(
    val name: String = "",
    val surname: String = "",
    val birthDate: Long? = null,
    val zone: String = "" // New field for Senior Sitting Adesione form
)
