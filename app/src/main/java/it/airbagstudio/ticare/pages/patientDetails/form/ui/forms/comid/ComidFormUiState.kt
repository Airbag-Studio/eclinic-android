package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData

/**
 * Represents the UI state for the COMID form screen.
 */
sealed interface ComidFormUiState {
    object Loading : ComidFormUiState
    data class Error(val message: String) : ComidFormUiState
    data class Editing(
        val form: COMIDForm,
        val patientData: PatientData = form.patientData, // Convenience access
        val isFormValid: Boolean = false,
        val isSaving: Boolean = false,
        // Specific validation flags for COMID if any, e.g., birth date for patient
        val isBirthDateValid: Boolean = true // Assuming patient birth date is still relevant
    ) : ComidFormUiState

    data class Saved(val savedForm: COMIDForm) : ComidFormUiState
}
