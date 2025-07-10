package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType

/**
 * Classe che centralizza le domande del form CBI.
 * Contiene le domande per tutte le sezioni del form.
 */
object CbiQuestions {
    
    /**
     * Restituisce le domande per una specifica sezione.
     *
     * @param sectionType Tipo di sezione
     * @return Lista di domande per la sezione specificata
     */
    fun getQuestionsForSection(sectionType: SectionType): List<QuestionResponse> {
        return when (sectionType) {
            SectionType.OBJECTIVE -> objectiveBurdenQuestions
            SectionType.PSYCHOLOGICAL -> psychologicalBurdenQuestions
            SectionType.PHYSICAL -> physicalBurdenQuestions
            SectionType.SOCIAL -> socialBurdenQuestions
            SectionType.EMOTIONAL -> emotionalBurdenQuestions
        }
    }
    
    /**
     * Restituisce tutte le domande per tutte le sezioni.
     *
     * @return Mappa con chiave il tipo di sezione e valore la lista di domande
     */
    fun getAllQuestions(): Map<SectionType, List<QuestionResponse>> {
        return mapOf(
            SectionType.OBJECTIVE to objectiveBurdenQuestions,
            SectionType.PSYCHOLOGICAL to psychologicalBurdenQuestions,
            SectionType.PHYSICAL to physicalBurdenQuestions,
            SectionType.SOCIAL to socialBurdenQuestions,
            SectionType.EMOTIONAL to emotionalBurdenQuestions
        )
    }
    
    /**
     * Domande per il Carico Oggettivo (domande 1-5).
     * Misura il carico associato alla restrizione di tempo per il caregiver.
     */
    private val objectiveBurdenQuestions = listOf(
        QuestionResponse(
            questionId = 1,
            questionText = "La persona che assisto ha bisogno del mio aiuto per svolgere molte delle abituali attività quotidiane"
        ),
        QuestionResponse(
            questionId = 2,
            questionText = "La persona che assisto è dipendente da me"
        ),
        QuestionResponse(
            questionId = 3,
            questionText = "Devo vigilarlo/sorvegliarla costantemente"
        ),
        QuestionResponse(
            questionId = 4,
            questionText = "Devo assisterlo/a anche per molte delle più semplici attività quotidiane (vestirlo/a, lavarlo/a, uso dei servizi igienici)"
        ),
        QuestionResponse(
            questionId = 5,
            questionText = "Non riesco ad avere un minuto di libertà dai miei compiti di assistenza"
        )
    )
    
    /**
     * Domande per il Carico Psicologico (domande 6-10).
     * Valuta la percezione del caregiver di sentirsi tagliato fuori rispetto alle aspettative.
     */
    private val psychologicalBurdenQuestions = listOf(
        QuestionResponse(
            questionId = 6,
            questionText = "Sento che sto trascurando gli altri membri della mia famiglia"
        ),
        QuestionResponse(
            questionId = 7,
            questionText = "Non riesco a dedicarmi al lavoro e alle responsabilità familiari con la stessa intensità di prima"
        ),
        QuestionResponse(
            questionId = 8,
            questionText = "La mia vita sociale ne ha risentito"
        ),
        QuestionResponse(
            questionId = 9,
            questionText = "Mi sento emotivamente svuotato/a a causa del mio ruolo di assistente"
        ),
        QuestionResponse(
            questionId = 10,
            questionText = "Mi aspettavo che le cose andassero diversamente a questo punto della mia vita"
        )
    )
    
    /**
     * Domande per il Carico Fisico (domande 11-14).
     * Descrive sensazioni di fatica cronica e problemi di salute somatica.
     */
    private val physicalBurdenQuestions = listOf(
        QuestionResponse(
            questionId = 11,
            questionText = "Non riesco a dormire a sufficienza"
        ),
        QuestionResponse(
            questionId = 12,
            questionText = "La mia salute ne ha risentito"
        ),
        QuestionResponse(
            questionId = 13,
            questionText = "Il compito di assisterlo/a mi ha reso fisicamente malato/a"
        ),
        QuestionResponse(
            questionId = 14,
            questionText = "Sono fisicamente stanco/a"
        )
    )
    
    /**
     * Domande per il Carico Sociale (domande 15-19).
     * Misura la percezione di un conflitto di ruolo.
     */
    private val socialBurdenQuestions = listOf(
        QuestionResponse(
            questionId = 15,
            questionText = "Non vado d'accordo con gli altri membri della famiglia come di consueto"
        ),
        QuestionResponse(
            questionId = 16,
            questionText = "Le mie attività di assistenza hanno creato sentimenti negativi verso gli altri membri della famiglia"
        ),
        QuestionResponse(
            questionId = 17,
            questionText = "Non sono soddisfatto/a del modo in cui gli altri membri della famiglia stanno contribuendo"
        ),
        QuestionResponse(
            questionId = 18,
            questionText = "Mi sento in imbarazzo per il comportamento della persona che assisto"
        ),
        QuestionResponse(
            questionId = 19,
            questionText = "Mi sento arrabbiato/a per le mie interazioni con altri membri della famiglia"
        )
    )
    
    /**
     * Domande per il Carico Emotivo (domande 20-24).
     * Valuta i sentimenti verso il paziente, indotti da comportamenti imprevedibili.
     */
    private val emotionalBurdenQuestions = listOf(
        QuestionResponse(
            questionId = 20,
            questionText = "Mi sento imbarazzato/a dal comportamento della persona che assisto"
        ),
        QuestionResponse(
            questionId = 21,
            questionText = "Mi vergogno di lui/lei"
        ),
        QuestionResponse(
            questionId = 22,
            questionText = "Provo del risentimento verso di lui/lei"
        ),
        QuestionResponse(
            questionId = 23,
            questionText = "Non mi sento a mio agio quando ho amici a casa"
        ),
        QuestionResponse(
            questionId = 24,
            questionText = "Mi arrabbio per le mie interazioni con lui/lei"
        )
    )
}
