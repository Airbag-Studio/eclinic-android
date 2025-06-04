package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta la risposta a una singola domanda del form.
 *
 * @property questionId ID della domanda
 * @property questionText Testo della domanda
 * @property score Punteggio selezionato (0-4)
 */
@Serializable
data class QuestionResponse(
    val questionId: Int,
    val questionText: String,
    val score: Int? = null // Changed to nullable, default null means unanswered
)
