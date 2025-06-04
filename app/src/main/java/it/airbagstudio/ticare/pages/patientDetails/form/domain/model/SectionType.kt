package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta i tipi di sezione del form CBI.
 */
@Serializable
enum class SectionType {
    OBJECTIVE, // Carico Oggettivo (domande 1-5)
    PSYCHOLOGICAL, // Carico Psicologico (domande 6-10)
    PHYSICAL, // Carico Fisico (domande 11-14)
    SOCIAL, // Carico Sociale (domande 15-19)
    EMOTIONAL // Carico Emotivo (domande 20-24)
}
