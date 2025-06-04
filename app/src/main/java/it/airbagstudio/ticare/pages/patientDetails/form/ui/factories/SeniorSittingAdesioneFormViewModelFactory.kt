package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingadesione.SeniorSittingAdesioneFormViewModel

class SeniorSittingAdesioneFormViewModelFactory(
    private val formRepository: FormRepository,
    private val formId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeniorSittingAdesioneFormViewModel::class.java)) {
            return SeniorSittingAdesioneFormViewModel(formRepository, formId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for SeniorSittingAdesioneFormViewModelFactory")
    }
}
