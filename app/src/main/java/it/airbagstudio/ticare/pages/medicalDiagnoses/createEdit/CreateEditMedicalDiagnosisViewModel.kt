package it.airbagstudio.ticare.pages.medicalDiagnoses.createEdit

import android.text.format.DateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ch.ticare.eclinic.library.repository.MedicalDiagnosesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateEditMedicalDiagnosisViewModel @Inject constructor(
    private val medicalDiagnosisRepository: MedicalDiagnosesRepository
) : ViewModel() {

    private val _executeDate = MutableStateFlow<Date>(Date())
    val executeDate = _executeDate.asStateFlow()
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    var canWrite by mutableStateOf(false)



    fun setExecutedDate(date: Date){
        _executeDate.value = date
    }

    fun setDescription(description: String) {
        _description.value = description

    }
}