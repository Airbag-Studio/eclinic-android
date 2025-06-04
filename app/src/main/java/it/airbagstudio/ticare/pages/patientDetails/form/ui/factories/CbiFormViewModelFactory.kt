package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.CbiFormViewModel

class CbiFormViewModelFactory(
    private val formRepository: FormRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CbiFormViewModel::class.java)) {
            return CbiFormViewModel(formRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
