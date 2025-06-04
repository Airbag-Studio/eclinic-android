package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta una sezione del form CBI.
 *
 * @property type Tipo di sezione
 * @property questions Lista delle risposte alle domande della sezione
 * @property partialScore Punteggio parziale della sezione
 * @property correctionFactor Fattore di correzione applicato al punteggio (es. 1.25 per il Carico Fisico)
 */
@Serializable
data class CBISection(
    val type: SectionType,
    val questions: List<QuestionResponse> = emptyList(),
    val partialScore: Int = 0,
    val correctionFactor: Float = when(type) {
        SectionType.PHYSICAL -> 1.25f
        else -> 1.0f
    }
)
