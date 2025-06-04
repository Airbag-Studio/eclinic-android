package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos3gg

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData

sealed interface IPOS3ggFormUiState {
    object Loading : IPOS3ggFormUiState
    data class Error(val message: String) : IPOS3ggFormUiState
    data class Editing(
        val form: IPOS3ggForm = IPOS3ggForm(patientData = PatientData()), // Default empty form
        val isSaving: Boolean = false,
        val isFormValid: Boolean = false, // Overall form validity
        // Individual field validation flags can be added here if needed, e.g.,
        // val isPatientNameValid: Boolean = true,
        val isPatientBirthDateValid: Boolean = true, // Assuming birth date is still required
        val isCompilationTimestampValid: Boolean = true // Assuming compilation time is still required
        // No specific validation for IPOS3gg questions for now, as per "no scoring logic"
    ) : IPOS3ggFormUiState
    data class Saved(val savedForm: IPOS3ggForm) : IPOS3ggFormUiState
}
