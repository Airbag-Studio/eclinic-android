package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData

/**
 * Represents the UI state for the unified IPOS form screen.
 */
sealed interface IPOSFormUiState {
    object Loading : IPOSFormUiState
    data class Error(val message: String) : IPOSFormUiState
    data class Editing(
        val form: IPOSForm,
        val patientData: PatientData = form.patientData,
        val isFormValid: Boolean = false,
        val isSaving: Boolean = false,
        val isCompilationTimestampValid: Boolean = true,
        val validationErrors: List<String> = emptyList(),
        val invalidFieldKeys: Set<String> = emptySet()
    ) : IPOSFormUiState

    data class Saved(val savedForm: IPOSForm) : IPOSFormUiState
}