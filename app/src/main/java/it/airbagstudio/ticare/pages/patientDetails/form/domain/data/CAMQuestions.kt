package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CAMSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import kotlin.collections.map

object CAMQuestions {

    val introductoryText: String = """
        Scala di valutazione CAM (Confusion Assessment Method)

        Introduzione: si tratta di un metodo standardizzato di valutazione della "confusione", per individuare precocemente il delirium nei setting ad alto rischio. Si è dimostrato uno strumento sensibile, specifico, realizzabile e facile da utilizzare.

        La CAM necessita di poco tempo (almeno 5') per essere completata, consiste nella valutazione di 9 criteri estrapolati dal manuale Diagnostico Statistico dei Disordini Mentali (DSM-III-R). La scala è utilizzata in ambito psichiatrico, geriatrico, oncologico; sia ospedaliero che ambulatoriale.

        Le caratteristiche considerate di importanza diagnostica, evidenzia 9 criteri:
        1. Esordio acuto
        2. Deficit di attenzione  
        3. Disorganizzazione del pensiero
        4. Alterato livello di coscienza
        5. Disorientamento
        6. Deficit di memoria
        7. Disturbi di percezione
        8. Agitazione psicomotoria / Ritardo psicomotorio
        9. Alterazione del ritmo sonno-veglia (ritmo circadiano alterato)

        La diagnosi di Delirium richiede la presenza di 4 caratteristiche:
        - 1. Esordio acuto e andamento fluttuante (alterazione acuta o fluttuazione dello stato mentale)
        - 2. Deficit di attenzione (disattenzione) e alternativamente 3 o 4
        - 3. Disorganizzazione del pensiero  
        - 4. Alterato livello di coscienza

        Per definire lo stato di Delirium:
        In rosso le due caratteristiche, 1 e 2, che devono essere sempre presenti, abbinate alle caratteristiche 3 e/o 4.

        CAM versione abbreviata di valutazione
        Domini del Delirium valutati in uno score da 0 (assente) a 3 (presente)
    """.trimIndent()

    val sections: List<CAMSection> = listOf(
        CAMSection(
            title = "ESORDIO ACUTO e ANDAMENTO FLUTTUANTE (1 punto)",
            order = 1,
            questions = listOf(
                QuestionResponse(questionId = 11, questionText = "C'è evidenza di un cambiamento acuto dello stato mentale rispetto a prima?"),
                QuestionResponse(questionId = 12, questionText = "C'è una non normale fluttuazione del comportamento durante il giorno che tende a diminuire o ad aumentare d'intensità?")
            )
        ),
        CAMSection(
            title = "DEFICIT DI ATTENZIONE (1 punto)",
            order = 2,
            questions = listOf(
                QuestionResponse(questionId = 2, questionText = "La persona ha difficoltà ha focalizzare l'attenzione, ad es: è facilmente distraibile, fatica a seguire ciò che gli si sta dicendo?")
            )
        ),
        CAMSection(
            title = "DISORGANIZZAZIONE DEL PENSIERO (1 punto)",
            order = 3,
            questions = listOf(
                QuestionResponse(questionId = 3, questionText = "La persona presenta pensieri disorganizzati o incoerenti, ad es: sconnesse o irrilevanti conversazioni, non chiare o illogici flussi di idee, imprevedibili cambiamenti da soggetto a soggetto?")
            )
        ),
        CAMSection(
            title = "ALTERATO LIVELLO DI COSCIENZA (1 punto)",
            order = 4,
            questions = listOf(
                QuestionResponse(questionId = 4, questionText = "Complessivamente come valuti il livello di coscienza della persona, agitato, sedato, incosciente (allerta, soporoso, stuporoso, coma)?")
            )
        )
    )

    /**
     * Restituisce le sezioni iniziali del form CAM con tutte le risposte impostate a null (non risposte).
     * Utilizzato per inizializzare un nuovo form.
     *
     * @return Lista delle sezioni con risposte predefinite (non risposte).
     */
    fun getInitialSections(): List<CAMSection> {
        return sections.map { section ->
            section.copy(
                questions = section.questions.map { question ->
                    // Default to null (unanswered)
                    question.copy(score = null)
                }
            )
        }
    }
}
