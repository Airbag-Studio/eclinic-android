package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingnonadesione.SeniorSittingNonAdesioneFormViewModel

class SeniorSittingNonAdesioneFormViewModelFactory(
    private val formId: String?,
    private val oldFormRepository: OldFormRepository,
    private val patientData: PatientData
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeniorSittingNonAdesioneFormViewModel::class.java)) {
            return SeniorSittingNonAdesioneFormViewModel(formId, oldFormRepository, patientData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
