package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingadesione

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm

sealed interface SeniorSittingAdesioneFormUiState {
    object Loading : SeniorSittingAdesioneFormUiState
    data class Error(val message: String) : SeniorSittingAdesioneFormUiState
    data class Editing(
        val form: SeniorSittingAdesioneForm = SeniorSittingAdesioneForm(patientData = PatientData()),
        val isSaving: Boolean = false,
        val isFormValid: Boolean = false,
        // Validation flags for patient data (similar to other forms)
        val isPatientBirthDateValid: Boolean = true, // Assuming birth date is still part of PatientData shown
        val isCompilationTimestampValid: Boolean = true,
        // Specific validation for Senior Sitting questions can be added if needed,
        // e.g., ensuring all scaled questions are answered.
        // For now, isFormValid will depend on patient data and if all questions are touched.
    ) : SeniorSittingAdesioneFormUiState
    data class Saved(val savedForm: SeniorSittingAdesioneForm) : SeniorSittingAdesioneFormUiState
}
