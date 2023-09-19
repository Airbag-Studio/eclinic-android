package it.airbagstudio.ticare.pages.patientsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.ui.components.PatientListItem
import javax.inject.Inject

@HiltViewModel
class PatientListScreenViewModel @Inject constructor(): ViewModel() {

    var query by mutableStateOf("")
    var patients by mutableStateOf(emptyList<PatientListItem>())
}