package it.airbagstudio.ticare.pages.medicalDiagnoses.createEdit

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.MedicalDiagnosisCreate
import ch.ticare.eclinic.library.entity.MedicalDiagnosisEdit
import ch.ticare.eclinic.library.repository.MedicalDiagnosesRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateEditMedicalDiagnosisViewModel @Inject constructor(
    private val medicalDiagnosisRepository: MedicalDiagnosesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _openDate = MutableStateFlow<Date>(Date())
    val openDate = _openDate.asStateFlow()
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()



    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _error.value = throwable.message
        _isLoading.value = false
    }

    private var userId: Int? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }

    }


    private var id: Int? = null

    fun setExecutedDate(date: Date){
        _openDate.value = date
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setMedicalDiagnosesId(id: Int){
        this.id = id
    }

    fun save(case: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isLoading.value = true
            createMedicalDiagnosis(case)
            _isLoading.value = false
        }

    }

    fun update(case: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isLoading.value = true
            updateMedicalDiagnosis(case)
            _isLoading.value = false
        }
    }

    fun close(case: String,closeReason: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isLoading.value = true
            closeMedicalDiagnosis(case,closeReason)
            _isLoading.value = false

        }
    }

    private suspend fun updateMedicalDiagnosis(case: String) {
        val medicalDiagnoseId = id ?: return
        val response = medicalDiagnosisRepository.editMedicalDiagnosis(
            MedicalDiagnosisEdit(
                id = medicalDiagnoseId,
                case = case,
                desc = description.value,
                openDate = DateFormat.format("yyyy-MM-dd HH:mm", openDate.value).toString()
            )
        )
        if (response.error != null){
            _error.value = response.error!!.desc
        }else{
            _error.value = null
            _isSuccess.value = true
        }
    }
    private suspend fun closeMedicalDiagnosis(case: String,closeReason: String){
        val medicalDiagnoseId = id ?: return
        val response = medicalDiagnosisRepository.editMedicalDiagnosis(
            MedicalDiagnosisEdit(
                id = medicalDiagnoseId,
                case = case,
                closeDate = DateFormat.format("yyyy-MM-dd HH:mm", Date()).toString(),
                closeReason = closeReason,
                closeUser = userId.toString(),
                desc = _description.value
            )
        )
        if (response.error != null){
            _error.value = response.error!!.desc
        }else{
            _error.value = null
            _isSuccess.value = true
        }
    }

    private suspend fun createMedicalDiagnosis(case: String){
        val response = medicalDiagnosisRepository.addMedicalSiagnosis(
            MedicalDiagnosisCreate(
                case = case,
                openDate = DateFormat.format("yyyy-MM-dd HH:mm", openDate.value).toString(),
                desc = description.value,

            )
        )
        if (response.error != null){
            _error.value = response.error!!.desc
        }else{
            _error.value = null
            _isSuccess.value = true
        }
    }

    fun clearData(){
        _openDate.value = Date()
        _description.value = ""
        _error.value = null
        _isSuccess.value = false
    }

    fun clearError(){
        _error.value = null
    }
}