package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingnonadesione.SeniorSittingNonAdesioneFormViewModel

class SeniorSittingNonAdesioneFormViewModelFactory(
    private val formId: String?,
    private val formRepository: FormRepository,
    private val patientData: PatientData
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeniorSittingNonAdesioneFormViewModel::class.java)) {
            return SeniorSittingNonAdesioneFormViewModel(formId, formRepository, patientData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
