package it.airbagstudio.ticare.pages.otherTreatments.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItem
import javax.inject.Inject

@HiltViewModel
class TreatmentSearchViewModel @Inject constructor(): ViewModel() {

    var query by mutableStateOf("")
    var results by mutableStateOf<List<OtherTreatmentItem>>(listOf())
}