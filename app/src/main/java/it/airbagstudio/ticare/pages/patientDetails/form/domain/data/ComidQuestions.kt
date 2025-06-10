package it.airbagstudio.ticare.pages.patientDetails.form.domain.data

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDSection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse

object ComidQuestions {

    val sections: List<COMIDSection> = listOf(
        COMIDSection(
            title = "1. Fattori dello stato di salute",
            order = 1,
            questions = listOf(
                QuestionResponse(questionId = 101, questionText = "Presenza di malattie croniche (>2) e/o sintomi inspiegabili"),
                QuestionResponse(questionId = 102, questionText = "Dolori cronici"),
                QuestionResponse(questionId = 103, questionText = "Allergie e/o intolleranze ai farmaci"),
                QuestionResponse(questionId = 104, questionText = "Assunzione di più farmaci (>5)"),
                QuestionResponse(questionId = 105, questionText = "Disturbi cognitivi")
            )
        ),
        COMIDSection(
            title = "2. Fattori socio-economici che aggravano lo stato di salute",
            order = 2,
            questions = listOf(
                QuestionResponse(questionId = 201, questionText = "Difficoltà sanitarie, di cura, trasporti o alimentazione"),
                QuestionResponse(questionId = 202, questionText = "Assenza o sfinimento del caregiver / tensioni familiari"),
                QuestionResponse(questionId = 203, questionText = "Difficoltà linguistiche, analfabetismo o barriere culturali"),
                QuestionResponse(questionId = 204, questionText = "Isolamento sociale"),
                QuestionResponse(questionId = 205, questionText = "Abitazione inadeguata o con barriere architettoniche")
            )
        ),
        COMIDSection(
            title = "3. Fattori di salute mentale aggravanti",
            order = 3,
            questions = listOf(
                QuestionResponse(questionId = 301, questionText = "Depressione e/o intenti suicidi"),
                QuestionResponse(questionId = 302, questionText = "Malattie psichiatriche / delirio / allucinazioni"),
                QuestionResponse(questionId = 303, questionText = "Dipendenze"),
                QuestionResponse(questionId = 304, questionText = "Ansia o angoscia che confonde il quadro clinico"),
                QuestionResponse(questionId = 305, questionText = "Funzioni mentali instabili durante la giornata")
            )
        ),
        COMIDSection(
            title = "4. Fattori comportamentali del paziente",
            order = 4,
            questions = listOf(
                QuestionResponse(questionId = 401, questionText = "Frequenti sollecitazioni alla rete"),
                QuestionResponse(questionId = 402, questionText = "Comunicazione ambivalente o conflittuale"),
                QuestionResponse(questionId = 403, questionText = "Preoccupazione eccessiva per sintomi o stato di salute"),
                QuestionResponse(questionId = 404, questionText = "Aggressività (verbale o fisica) o mutismo"),
                QuestionResponse(questionId = 405, questionText = "Opposizione o resistenza alle cure")
            )
        ),
        COMIDSection(
            title = "5. Fattori di instabilità",
            order = 5,
            questions = listOf(
                QuestionResponse(questionId = 501, questionText = "Percezione di un recente peggioramento"),
                QuestionResponse(questionId = 502, questionText = "Cambiamento dell’autonomia (ADL/IADL) nell’ultimo mese"),
                QuestionResponse(questionId = 503, questionText = "Periodo di transizione (diagnosi, ricovero, morte caregiver, ecc.)"),
                QuestionResponse(questionId = 504, questionText = "Cambiamento acuto delle capacità cognitive"),
                QuestionResponse(questionId = 505, questionText = "Imprevedibilità dello stato di salute")
            )
        ),
        COMIDSection(
            title = "6. Fattori relativi agli operatori e alla rete sanitaria",
            order = 6,
            questions = listOf(
                QuestionResponse(questionId = 601, questionText = "Moltitudine di operatori nella rete"),
                QuestionResponse(questionId = 602, questionText = "Basso grado di collaborazione tra attori della rete"),
                QuestionResponse(questionId = 603, questionText = "Incoerenze terapeutiche / presa in carico poco sensata"),
                QuestionResponse(questionId = 604, questionText = "Problemi assicurativi (rimborso limitato, ecc.)"),
                QuestionResponse(questionId = 605, questionText = "Stanchezza emotiva e/o fisica degli operatori")
            )
        )
    )

    fun getInitialSections(): List<COMIDSection> {
        return sections.map { section ->
            section.copy(
                questions = section.questions.map { question ->
                    question.copy(score = null) // No default selection - user must choose
                }
            )
        }
    }
}
