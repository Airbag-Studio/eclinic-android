package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingnonadesione

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneSection

sealed interface SeniorSittingNonAdesioneFormUiState {
    object Loading : SeniorSittingNonAdesioneFormUiState
    data class Error(val message: String) : SeniorSittingNonAdesioneFormUiState
    data class Editing(
        val formId: String?,
        val patientData: PatientData,
        val compilationTimestamp: Long,
        val sections: List<SeniorSittingNonAdesioneSection>,
        val isSaving: Boolean = false,
        // Validation state for Section 1 (at least one checkbox)
        val isSection1Valid: Boolean = true,
        // Validation state for Section 2 (open question)
        val isSection2Valid: Boolean = true,
        val generalError: String? = null // For overall form validation messages
    ) : SeniorSittingNonAdesioneFormUiState {
        val canSave: Boolean
            get() = isSection1Valid && isSection2Valid && !isSaving
    }
    data class Saved(val savedForm: SeniorSittingNonAdesioneForm) : SeniorSittingNonAdesioneFormUiState
}
