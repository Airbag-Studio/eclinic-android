package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingForm

/**
 * Represents the UI state for the unified Senior Sitting form screen.
 */
sealed interface SeniorSittingFormUiState {
    object Loading : SeniorSittingFormUiState
    data class Error(val message: String) : SeniorSittingFormUiState
    data class Editing(
        val form: SeniorSittingForm,
        val patientData: PatientData = form.patientData,
        val isFormValid: Boolean = false,
        val isSaving: Boolean = false,
        val isCompilationTimestampValid: Boolean = true,
        val validationErrors: List<String> = emptyList(),
        val invalidFieldKeys: Set<String> = emptySet()
    ) : SeniorSittingFormUiState

    data class Saved(val savedForm: SeniorSittingForm) : SeniorSittingFormUiState
}