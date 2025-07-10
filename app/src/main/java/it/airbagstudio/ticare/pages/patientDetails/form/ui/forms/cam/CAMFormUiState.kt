package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cam

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CAMForm

sealed interface CAMFormUiState {
    object Loading : CAMFormUiState
    data class Error(val message: String) : CAMFormUiState
    data class Editing(
        val form: CAMForm,
        val isBirthDateValid: Boolean = true, // Assuming birth date is the only validated field for now
        val isSaving: Boolean = false,
        val isFormValid: Boolean = false
    ) : CAMFormUiState
    data class Saved(val savedForm: CAMForm) : CAMFormUiState
}
