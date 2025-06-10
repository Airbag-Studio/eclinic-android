package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

/**
 * Enum for IPOS time periods
 */
enum class IPOSTimePeriod(val days: Int, val displayName: String) {
    DAYS_3(3, "3 giorni"),
    DAYS_7(7, "7 giorni");
    
    val introText: String
        get() = when (this) {
            DAYS_3 -> "Istruzioni per la compilazione: si prega di rispondere alle seguenti domande in base a quanto sperimentato negli ultimi 3 giorni."
            DAYS_7 -> "Istruzioni per la compilazione: si prega di rispondere alle seguenti domande in base a quanto sperimentato nell'ultima settimana."
        }
    
    val q1IntroText: String
        get() = when (this) {
            DAYS_3 -> "Elenca i tre principali problemi o preoccupazioni vissuti dal paziente negli ultimi 3 giorni:"
            DAYS_7 -> "Elenca i tre principali problemi o preoccupazioni vissuti dal paziente negli ultimi 7 giorni:"
        }
    
    val q2IntroText: String
        get() = when (this) {
            DAYS_3 -> "Valuta i seguenti sintomi in base a quanto hanno disturbato il paziente negli ultimi 3 giorni:"
            DAYS_7 -> "Valuta i seguenti sintomi in base a quanto hanno disturbato il paziente negli ultimi 7 giorni:"
        }
}