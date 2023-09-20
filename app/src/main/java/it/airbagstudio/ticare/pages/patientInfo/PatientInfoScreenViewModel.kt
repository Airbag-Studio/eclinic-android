package it.airbagstudio.ticare.pages.patientInfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import javax.inject.Inject

@HiltViewModel
class PatientInfoScreenViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val patientCod: String? = savedStateHandle[DestinationsArgs.PATIENT_COD]
}