package it.airbagstudio.ticare.pages.patientDetails.form.ui.factories

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid.ComidFormViewModel

/**
 * Factory for creating instances of [ComidFormViewModel].
 * This is used to provide dependencies to the ViewModel, such as the [FormRepository].
 */

@Suppress("UNCHECKED_CAST")
class ComidFormViewModelFactory(
    private val formRepository: FormRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComidFormViewModel::class.java)) {
            return ComidFormViewModel(formRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
