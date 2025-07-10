package it.airbagstudio.ticare.pages.patientDetails.form.domain.data


import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IDPallSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import kotlin.collections.map

/**
 * Oggetto che contiene tutte le domande e sezioni del form ID PALL.
 * Gestisce la struttura statica del questionario per l'identificazione delle situazioni
 * di cura necessitanti cure palliative.
 */
object IDPallQuestions {

    /**
     * Testo introduttivo del form ID PALL.
     */
    val introText = """
        ID-PALL® G

        IDentificazione delle situazioni di cura necessitanti cure PALLiative Generali.
        Le cure palliative generali vengono erogate da personale curante non specializzato in cure palliative in tutti gli ambienti di vita e di cura.

        Rispondete alle affermazioni seguenti relative alla situazione attuale del paziente:
    """.trimIndent()

    /**
     * Lista delle sezioni del form ID PALL con le relative domande.
     */
    val sections: List<IDPallSection> = listOf(
        IDPallSection(
            title = "ID PALL Generali",
            order = 1,
            questions = listOf(
                QuestionResponse(
                    questionId = 1,
                    questionText = "Sareste sorpresi se il paziente morisse nell'arco dei prossimi 12 mesi?"
                ),
                QuestionResponse(
                    questionId = 2,
                    questionText = "Il paziente è affetto da una malattia o un insieme di malattie/co-morbidità che limitano la sua prognosi e presenta (più scelte possibili):"
                ),
                QuestionResponse(
                    questionId = 21,
                    questionText = "un declino funzionale generale (con limitare reversibilità e maggiore necessità di cura di supporto)"
                ),
                QuestionResponse(
                    questionId = 22,
                    questionText = "una marcata instabilità negli ultimi 6 mesi (definita da: un sintomo non controllato dal punto di vista del paziente O lesioni da pressione stadio >= 3 O più episodi di stato confusionale acuto, d'infezione, d'ospedalizzazione non programmate O da caduta)"
                ),
                QuestionResponse(
                    questionId = 23,
                    questionText = "una sofferenza psicosociale o esistenziale (propria o dei familiari)"
                ),
                QuestionResponse(
                    questionId = 24,
                    questionText = "un bisogno d'accompagnamento nella presa di decisioni dell'ultima fase della vita"
                ),
                QuestionResponse(
                    questionId = 3,
                    questionText = "Interruzione, effettiva, o prese in considerazione, dei trattamenti curativi o di misure di supporto vitale (p.es: ventilazione artificiale, dialisi, alimentazione e/o idratazione artificiale)"
                ),
                QuestionResponse(
                    questionId = 4,
                    questionText = "Richiesta di cure di comfort/palliative da parte del paziente, dei familiari o personale curante"
                )
            )
        ),
        IDPallSection(
            title = "ID PALL Specializzate",
            order = 2,
            questions = listOf(
                QuestionResponse(
                    questionId = 101,
                    questionText = "Presenza di almeno un sintomo severo e persistente, incluso il dolore, non rispondente in modo soddisfacente al trattamento entro le 48h"
                ),
                QuestionResponse(
                    questionId = 102,
                    questionText = "Difficoltà di valutazione dei sintomi fisici o delle problematiche psicologiche, sociali o spirituali"
                ),
                QuestionResponse(
                    questionId = 103,
                    questionText = "Disaccordo o incertezza del paziente, dei familiari o del personale curante concernenti per es. i trattamenti medici, la rianimazione o le decisioni complesse"
                ),
                QuestionResponse(
                    questionId = 104,
                    questionText = "Importante sofferenza psicosociale o esistenziale del paziente (es.: richiesta di suicidio assistito, desiderio di morte, perdita del senso/speranza di vita, sentimento d'isolamento, sensazione di essere un peso)"
                ),
                QuestionResponse(
                    questionId = 105,
                    questionText = "Importante sofferenza psicosociale o esistenziale dei familiari (es.: difficoltà circa la progressione della malattia o alla morte, sensazione di sfinimento importante)"
                ),
                QuestionResponse(
                    questionId = 106,
                    questionText = "Presa in considerazione di una sedazione palliativa (diminuzione dello stato di coscienza con l'aiuto di farmaci specifici atti ad alleviare un sintomo refrattario)"
                ),
                QuestionResponse(
                    questionId = 107,
                    questionText = "Progetto terapeutico/di cura difficile da concordare con il paziente e i familiari o direttive anticipate difficili da redigere"
                ),
                QuestionResponse(
                    questionId = 108,
                    questionText = "Il paziente, i familiari o il personale curante potrebbero, secondo voi, beneficiare di un intervento di specialisti in cure palliative"
                )
            )
        )
    )

    /**
     * Restituisce le sezioni iniziali del form con tutte le risposte impostate a "No" (score = 0).
     * Utilizzato per inizializzare un nuovo form.
     *
     * @return Lista delle sezioni con risposte predefinite.
     */
    fun getInitialSections(): List<IDPallSection> {
        return sections.map { section ->
            section.copy(
                questions = section.questions.map { question ->
                    // Default to null (unanswered) instead of 0 ("no")
                    question.copy(score = null)
                }
            )
        }
    }
}
