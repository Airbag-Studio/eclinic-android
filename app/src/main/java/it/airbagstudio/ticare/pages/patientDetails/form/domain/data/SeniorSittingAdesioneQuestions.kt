package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneSection

object SeniorSittingAdesioneQuestions {

    // Question IDs
    const val Q1_GRADIMENTO_ID = 1001
    const val Q2_UTILITA_SERVIZIO_ID = 1002
    const val Q3_QUALITA_OFFERTA_ID = 1003
    const val Q4_UTILITA_SGRAVO_CAREGIVER_ID = 1004
    const val Q5_UTILITA_CONTATTI_SOCIALI_ID = 1005
    const val Q6_INCIDENZA_COSTO_ID = 1006
    const val Q7_AIUTO_SCUDO_ID = 1007
    const val Q8_SUGGERIMENTI_ID = 1008

    val formTitle = "Senior Sitting – Adesione"
    // No specific intro or closing text provided in the spec for the form itself,
    // these are usually part of the screen layout.

    val initialSections: List<SeniorSittingAdesioneSection> = listOf(
        SeniorSittingAdesioneSection(
            sectionId = "scaled_questions",
            title = "Valutazione Servizio (Scala 1-5)", // Generic title for scaled questions
            questions = listOf(
                QuestionResponse(questionId = Q1_GRADIMENTO_ID, questionText = "Quanto il servizio ricevuto è stato di suo gradimento?"),
                QuestionResponse(questionId = Q2_UTILITA_SERVIZIO_ID, questionText = "Quanto reputa utile il servizio ricevuto?"),
                QuestionResponse(questionId = Q3_QUALITA_OFFERTA_ID, questionText = "Quanto reputa di qualità l'offerta/aiuto ricevuto dagli operatori coinvolti?"),
                QuestionResponse(questionId = Q4_UTILITA_SGRAVO_CAREGIVER_ID, questionText = "Quanto ritiene essere stato utile l'aiuto ricevuto per sgravarsi dal suo carico assistenziale e ridurre lo stress percepito? (inserire valore zero nel caso in cui non presente un caregiver)"),
                QuestionResponse(questionId = Q5_UTILITA_CONTATTI_SOCIALI_ID, questionText = "Quanto ritiene essere stato utile l'aiuto ricevuto per favorire i Suoi contatti sociali?"),
                QuestionResponse(questionId = Q6_INCIDENZA_COSTO_ID, questionText = "Quanto NON ha inciso il costo di 20 fr-. orari sulle sue finanze?"),
                QuestionResponse(questionId = Q7_AIUTO_SCUDO_ID, questionText = "In cosa Scudo potrebbe aiutarla maggiormente per farla sentire meglio e ridurre il suo carico assistenziale?", score = null) // Moved Q7 here
            )
        ),
        SeniorSittingAdesioneSection(
            sectionId = "management_organization_questions", // Changed sectionId
            title = "Gestione – Organizzazione", // Changed title
            questions = listOf(
                QuestionResponse(questionId = Q8_SUGGERIMENTI_ID, questionText = "Suggerimenti, osservazioni:", score = null) // Q8 only, updated prompt slightly for clarity as section title is now more specific
            )
        )
    )
}
