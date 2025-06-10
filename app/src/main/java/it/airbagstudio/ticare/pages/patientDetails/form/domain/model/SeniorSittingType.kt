package it.airbagstudio.ticare.pages.patientDetails.form.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SeniorSittingType(val typeName: String, val displayName: String, val formTitle: String) {
    ADESIONE(
        typeName = "SeniorSittingAdesione", 
        displayName = "Adesione", 
        formTitle = "Senior Sitting – Adesione"
    ),
    NON_ADESIONE(
        typeName = "SENIOR_SITTING_NON_ADESIONE", 
        displayName = "Non Adesione", 
        formTitle = "Progetto Senior Sitting per il supporto al famigliare curante - Non adesione"
    )
}