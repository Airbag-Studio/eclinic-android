package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse

object IPOS3ggQuestions {

    // Unique Question IDs
    // Q1: Main problems/concerns (free text)
    const val Q1_CONCERN_1_ID = 101
    const val Q1_CONCERN_2_ID = 102
    const val Q1_CONCERN_3_ID = 103

    // Q2: Disturbing symptoms (0-4 scale)
    const val Q2_DOLORE_ID = 201
    const val Q2_MANCANZA_DI_FIATO_ID = 202
    const val Q2_DEBOLEZZA_MANCANZA_ENERGIA_ID = 203
    const val Q2_NAUSEA_ID = 204
    const val Q2_VOMITO_ID = 205
    const val Q2_SCARSO_APPETITO_ID = 206
    const val Q2_STITICHEZZA_ID = 207
    const val Q2_PROBLEMI_CAVO_ORALE_ID = 208
    const val Q2_SONNOLENZA_ID = 209
    const val Q2_PROBLEMI_MOBILIZZAZIONE_ID = 210

    // Q2b: Additional symptoms (free text + 0-4 scale)
    // For each additional symptom, one QuestionResponse will hold the text (in questionText)
    // and its score (in score).
    const val Q2B_ADDITIONAL_SYMPTOM_1_ID = 211
    const val Q2B_ADDITIONAL_SYMPTOM_2_ID = 212
    const val Q2B_ADDITIONAL_SYMPTOM_3_ID = 213

    // Q3-Q9: Emotional, relational, spiritual, practical (0-4 scale)
    const val Q3_ANSIA_MALATTIA_TERAPIE_ID = 301
    const val Q4_ANSIA_CARI_ID = 302
    const val Q5_DEPRESSIONE_ID = 303
    const val Q6_PACE_SE_STESSO_ID = 304
    const val Q7_CONDIVIDERE_STATI_ANIMO_ID = 305
    const val Q8_INFO_RICEVUTE_ID = 306
    const val Q9_GESTIONE_PROBLEMI_PRATICI_ID = 307

    // Q10: Mode of completion (single choice)
    const val Q10_MODALITA_COMPILAZIONE_ID = 1001

    val formIntroText = "Istruzioni per la compilazione: si prega di rispondere alle seguenti domande in base a quanto sperimentato negli ultimi 3 giorni."
    val formClosingMessage = "Se si sente preoccupato per qualsiasi aspetto sollevato dal questionario, si senta libero di parlarne con il suo medico o infermiere."

    val initialSections: List<IPOS3ggSection> = listOf(
        IPOS3ggSection(
            sectionId = "Q1",
            title = "Q1 – Problemi o preoccupazioni principali",
            questions = listOf(
                QuestionResponse(questionId = Q1_CONCERN_1_ID, questionText = "" /* User input stored here */),
                QuestionResponse(questionId = Q1_CONCERN_2_ID, questionText = "" /* User input stored here */),
                QuestionResponse(questionId = Q1_CONCERN_3_ID, questionText = "" /* User input stored here */)
            )
        ),
        IPOS3ggSection(
            sectionId = "Q2",
            title = "Q2 – Sintomi disturbanti (Scala da 0 a 4: 0 = Per nulla, 4 = Opprimente)",
            questions = listOf(
                QuestionResponse(questionId = Q2_DOLORE_ID, questionText = "Dolore"),
                QuestionResponse(questionId = Q2_MANCANZA_DI_FIATO_ID, questionText = "Mancanza di fiato"),
                QuestionResponse(questionId = Q2_DEBOLEZZA_MANCANZA_ENERGIA_ID, questionText = "Debolezza o mancanza di energia"),
                QuestionResponse(questionId = Q2_NAUSEA_ID, questionText = "Nausea"),
                QuestionResponse(questionId = Q2_VOMITO_ID, questionText = "Vomito"),
                QuestionResponse(questionId = Q2_SCARSO_APPETITO_ID, questionText = "Scarso appetito"),
                QuestionResponse(questionId = Q2_STITICHEZZA_ID, questionText = "Stitichezza"),
                QuestionResponse(questionId = Q2_PROBLEMI_CAVO_ORALE_ID, questionText = "Problemi al cavo orale"),
                QuestionResponse(questionId = Q2_SONNOLENZA_ID, questionText = "Sonnolenza"),
                QuestionResponse(questionId = Q2_PROBLEMI_MOBILIZZAZIONE_ID, questionText = "Problemi di mobilizzazione")
            )
        ),
        IPOS3ggSection(
            sectionId = "Q2b",
            title = "Q2b – Sintomi aggiuntivi (campo + scala da 0 a 4)",
            questions = listOf(
                // For Q2b, questionText will store the symptom name, score its rating.
                // The UI will provide prompts like "Sintomo aggiuntivo 1".
                QuestionResponse(questionId = Q2B_ADDITIONAL_SYMPTOM_1_ID, questionText = "" /* Symptom name */),
                QuestionResponse(questionId = Q2B_ADDITIONAL_SYMPTOM_2_ID, questionText = "" /* Symptom name */),
                QuestionResponse(questionId = Q2B_ADDITIONAL_SYMPTOM_3_ID, questionText = "" /* Symptom name */)
            )
        ),
        IPOS3ggSection(
            sectionId = "Q3_Q9",
            title = "Q3 a Q9 – Stato emotivo, relazionale, spirituale e pratico (Scala da 0 a 4)",
            questions = listOf(
                QuestionResponse(questionId = Q3_ANSIA_MALATTIA_TERAPIE_ID, questionText = "Q3. Ansia o preoccupazione per la propria malattia o le terapie"),
                QuestionResponse(questionId = Q4_ANSIA_CARI_ID, questionText = "Q4. Ansia o preoccupazione da parte dei propri cari"),
                QuestionResponse(questionId = Q5_DEPRESSIONE_ID, questionText = "Q5. Depressione"),
                QuestionResponse(questionId = Q6_PACE_SE_STESSO_ID, questionText = "Q6. Pace con sé stesso"),
                QuestionResponse(questionId = Q7_CONDIVIDERE_STATI_ANIMO_ID, questionText = "Q7. Capacità di condividere gli stati d’animo con i propri cari"),
                QuestionResponse(questionId = Q8_INFO_RICEVUTE_ID, questionText = "Q8. Completezza delle informazioni ricevute"),
                QuestionResponse(questionId = Q9_GESTIONE_PROBLEMI_PRATICI_ID, questionText = "Q9. Gestione dei problemi pratici, personali o economici derivanti dalla malattia")
            )
        ),
        IPOS3ggSection(
            sectionId = "Q10",
            title = "Q10 – Modalità di compilazione del questionario",
            questions = listOf(
                QuestionResponse(questionId = Q10_MODALITA_COMPILAZIONE_ID, questionText = "Modalità di compilazione") // Score will be 0, 1, or 2
            )
        )
    )

    val q10Options: List<String> = listOf(
        "Da solo",
        "Con l'aiuto di un familiare o un amico",
        "Con l'aiuto di un membro dello staff"
    )
}
