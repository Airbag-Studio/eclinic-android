package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingType

object SeniorSittingQuestions {

    // Adesione Question IDs
    const val Q1_GRADIMENTO_ID = 1001
    const val Q2_UTILITA_SERVIZIO_ID = 1002
    const val Q3_QUALITA_OFFERTA_ID = 1003
    const val Q4_UTILITA_SGRAVO_CAREGIVER_ID = 1004
    const val Q5_UTILITA_CONTATTI_SOCIALI_ID = 1005
    const val Q6_INCIDENZA_COSTO_ID = 1006
    const val Q7_AIUTO_SCUDO_ID = 1007
    const val Q8_SUGGERIMENTI_ID = 1008

    // Non-Adesione Question IDs (starting from 2000 to avoid conflicts)
    const val NA_Q1_AUTONOMIA_ID = 2001
    const val NA_Q2_ALTRI_AIUTI_ID = 2002
    const val NA_Q3_NON_PERTINENTE_ID = 2003
    const val NA_Q4_COSTO_SOSTENERE_ID = 2004
    const val NA_Q5_COSTO_CARO_ID = 2005
    const val NA_Q6_NON_SUFFICIENTE_ID = 2006
    const val NA_Q7_FASCIA_ORARIA_ID = 2007
    const val NA_Q8_COMPETENZA_ID = 2008
    const val NA_Q9_NON_RISPONDE_ID = 2009
    const val NA_Q10_SUGGERIMENTI_ID = 2010

    fun getFormTitle(type: SeniorSittingType): String = type.formTitle

    fun getInitialSections(type: SeniorSittingType): List<SeniorSittingSection> {
        return when (type) {
            SeniorSittingType.ADESIONE -> getAdesioneSections()
            SeniorSittingType.NON_ADESIONE -> getNonAdesioneSections()
        }
    }

    private fun getAdesioneSections(): List<SeniorSittingSection> = listOf(
        SeniorSittingSection(
            sectionId = "scaled_questions",
            title = "Valutazione Servizio (Scala 1-5)",
            order = 1,
            questions = listOf(
                QuestionResponse(questionId = Q1_GRADIMENTO_ID, questionText = "Quanto il servizio ricevuto è stato di suo gradimento?"),
                QuestionResponse(questionId = Q2_UTILITA_SERVIZIO_ID, questionText = "Quanto reputa utile il servizio ricevuto?"),
                QuestionResponse(questionId = Q3_QUALITA_OFFERTA_ID, questionText = "Quanto reputa di qualità l'offerta/aiuto ricevuto dagli operatori coinvolti?"),
                QuestionResponse(questionId = Q4_UTILITA_SGRAVO_CAREGIVER_ID, questionText = "Quanto ritiene essere stato utile l'aiuto ricevuto per sgravarsi dal suo carico assistenziale e ridurre lo stress percepito? (inserire valore zero nel caso in cui non presente un caregiver)"),
                QuestionResponse(questionId = Q5_UTILITA_CONTATTI_SOCIALI_ID, questionText = "Quanto ritiene essere stato utile l'aiuto ricevuto per favorire i Suoi contatti sociali?"),
                QuestionResponse(questionId = Q6_INCIDENZA_COSTO_ID, questionText = "Quanto NON ha inciso il costo di 20 fr-. orari sulle sue finanze?"),
                QuestionResponse(questionId = Q7_AIUTO_SCUDO_ID, questionText = "In cosa Scudo potrebbe aiutarla maggiormente per farla sentire meglio e ridurre il suo carico assistenziale?", score = null)
            )
        ),
        SeniorSittingSection(
            sectionId = "management_organization_questions",
            title = "Gestione – Organizzazione",
            order = 2,
            questions = listOf(
                QuestionResponse(questionId = Q8_SUGGERIMENTI_ID, questionText = "Suggerimenti, osservazioni:", score = null)
            )
        )
    )

    private fun getNonAdesioneSections(): List<SeniorSittingSection> = listOf(
        SeniorSittingSection(
            sectionId = "non_adesione_reasons",
            title = "Motivi della non adesione",
            order = 1,
            introductoryText = "Ringraziandola per la collaborazione. Al fine di poter migliorare la qualità del servizio proposto le chiediamo di segnare le affermazioni che ritiene pertinenti rispetto alla sua decisione di non aderire al servizio proposto:",
            questions = listOf(
                QuestionResponse(questionId = NA_Q1_AUTONOMIA_ID, questionText = "mi sento in grado di gestire in autonomia la situazione"),
                QuestionResponse(questionId = NA_Q2_ALTRI_AIUTI_ID, questionText = "ricevo altri aiuti e non necessito del vostro"),
                QuestionResponse(questionId = NA_Q3_NON_PERTINENTE_ID, questionText = "il supporto proposto non è pertinente/adeguato per la mia situazione"),
                QuestionResponse(questionId = NA_Q4_COSTO_SOSTENERE_ID, questionText = "non posso sostenere il costo del servizio"),
                QuestionResponse(questionId = NA_Q5_COSTO_CARO_ID, questionText = "reputo il costo del servizio troppo caro"),
                QuestionResponse(questionId = NA_Q6_NON_SUFFICIENTE_ID, questionText = "il supporto proposto non è sufficiente a garantire l'aiuto di cui necessito"),
                QuestionResponse(questionId = NA_Q7_FASCIA_ORARIA_ID, questionText = "la fascia oraria proposta non soddisfa le mie necessità"),
                QuestionResponse(questionId = NA_Q8_COMPETENZA_ID, questionText = "reputo l'operatore coinvolto non sufficientemente competente"),
                QuestionResponse(questionId = NA_Q9_NON_RISPONDE_ID, questionText = "Non desidera rispondere")
            )
        ),
        SeniorSittingSection(
            sectionId = "non_adesione_suggestions",
            title = "Gestione – Organizzazione",
            order = 2,
            questions = listOf(
                QuestionResponse(questionId = NA_Q10_SUGGERIMENTI_ID, questionText = "Suggerimenti, osservazioni:")
            )
        )
    )
}