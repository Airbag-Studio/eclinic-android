package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos7gg // Package name changed

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm // Import changed
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData

sealed interface IPOS7ggFormUiState { // Interface name changed
    object Loading : IPOS7ggFormUiState // State name changed
    data class Error(val message: String) : IPOS7ggFormUiState // State name changed
    data class Editing(
        val form: IPOS7ggForm = IPOS7ggForm(patientData = PatientData()), // Changed to IPOS7ggForm
        val isSaving: Boolean = false,
        val isFormValid: Boolean = false,
        val isPatientBirthDateValid: Boolean = true,
        val isCompilationTimestampValid: Boolean = true
    ) : IPOS7ggFormUiState // State name changed
    data class Saved(val savedForm: IPOS7ggForm) : IPOS7ggFormUiState // State name changed, changed to IPOS7ggForm
}
