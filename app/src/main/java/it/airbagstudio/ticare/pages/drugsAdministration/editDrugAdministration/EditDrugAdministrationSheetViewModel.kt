package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditDrugAdministrationSheetViewModel @Inject constructor(): ViewModel() {

    var notesText by mutableStateOf("test")
}