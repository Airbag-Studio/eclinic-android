package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.pacic

import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PACICForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.IPOSFormUiState

sealed interface PACICFormUiState {

    object Loading : PACICFormUiState
    data class Error(val message: String) : PACICFormUiState
    data class Editing(
        val form: PACICForm,
        val patientData: PatientData = form.patientData,
        val isFormValid: Boolean = true,
        val isSaving: Boolean = false,
        val isCompilationTimestampValid: Boolean = true,
        val validationErrors: List<String> = emptyList(),
        val invalidFieldKeys: Set<String> = emptySet()
    ) : PACICFormUiState

    data class Saved(val savedForm: PACICForm) : PACICFormUiState
}