package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse

/**
 * Classe che centralizza le domande del form PACICS.
 * Scala di valutazione: mai 1, raramente 2, a volte 3, spesso 4, sempre 5
 */
object PACICSQuestions {

    val introText = "Nel corso degli ultimi 6 mesi, con quale frequenza è accaduto che:"

    fun getQuestions(): List<QuestionResponse> {
        return listOf(
            QuestionResponse(
                questionId = 1,
                questionText = "Le è stato chiesto se poteva seguire le cure prescritte nella sua vita quotidiana?"
            ),
            QuestionResponse(
                questionId = 2,
                questionText = "Le è stato chiesto di partecipare alla definizione del suo piano di cura?"
            ),
            QuestionResponse(
                questionId = 3,
                questionText = "Il suo piano di cura rispecchiava le sue preferenze?"
            ),
            QuestionResponse(
                questionId = 4,
                questionText = "Ha ricevuto una copia scritta (o elettronica) del suo piano terapeutico interprofessionale?"
            ),
            QuestionResponse(
                questionId = 5,
                questionText = "Le sono stati proposti obiettivi concreti da raggiungere?"
            ),
            QuestionResponse(
                questionId = 6,
                questionText = "Le è stato chiesto come si sente riguardo alla gestione della sua condizione?"
            ),
            QuestionResponse(
                questionId = 7,
                questionText = "Le è stato chiesto di portare con sé informazioni su visite, farmaci o cure ricevute da altri?"
            ),
            QuestionResponse(
                questionId = 8,
                questionText = "È stata/o contattata/o per sapere come stava tra una visita e l'altra?"
            ),
            QuestionResponse(
                questionId = 9,
                questionText = "Le è stato detto/a che tipo di attenzioni mediche doveva ricevere?"
            ),
            QuestionResponse(
                questionId = 10,
                questionText = "Ha ricevuto aiuto per organizzare la sua cura?"
            ),
            QuestionResponse(
                questionId = 11,
                questionText = "Le è stato chiesto se voleva parlare con un gruppo di supporto o con altri pazienti con la sua stessa condizione?"
            )
        )
    }
}