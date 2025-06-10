package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneSection

object SeniorSittingNonAdesioneQuestions {

    val formTitle = "Senior Sitting - Non adesione"

    val sections: List<SeniorSittingNonAdesioneSection> = listOf(
        SeniorSittingNonAdesioneSection(
            title = "Motivi della non adesione",
            order = 1,
            introductoryText = "Ringraziandola per la collaborazione. Al fine di poter migliorare la qualità del servizio proposto le chiediamo di segnare le affermazioni che ritiene pertinenti rispetto alla sua decisione di non aderire al servizio proposto:",
            questions = listOf(
                QuestionResponse(questionId = 1, questionText = "mi sento in grado di gestire in autonomia la situazione"),
                QuestionResponse(questionId = 2, questionText = "ricevo altri aiuti e non necessito del vostro"),
                QuestionResponse(questionId = 3, questionText = "il supporto proposto non è pertinente/adeguato per la mia situazione"),
                QuestionResponse(questionId = 4, questionText = "non posso sostenere il costo del servizio"),
                QuestionResponse(questionId = 5, questionText = "reputo il costo del servizio troppo caro"),
                QuestionResponse(questionId = 6, questionText = "il supporto proposto non è sufficiente a garantire l'aiuto di cui necessito"),
                QuestionResponse(questionId = 7, questionText = "la fascia oraria proposta non soddisfa le mie necessità"),
                QuestionResponse(questionId = 8, questionText = "reputo l'operatore coinvolto non sufficientemente competente"),
                QuestionResponse(questionId = 9, questionText = "Non desidera rispondere")
            )
        ),
        SeniorSittingNonAdesioneSection(
            title = "Gestione – Organizzazione",
            order = 2,
            questions = listOf(
                QuestionResponse(questionId = 10, questionText = "Suggerimenti, osservazioni:")
            )
        )
    )
}
