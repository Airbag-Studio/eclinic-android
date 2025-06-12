package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos3gg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOS3ggQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS3ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset

class IPOS3ggFormViewModel(
    private val oldFormRepository: OldFormRepository,
    private val formId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow<IPOS3ggFormUiState>(IPOS3ggFormUiState.Loading)
    val uiState: StateFlow<IPOS3ggFormUiState> = _uiState.asStateFlow()

    init {
        loadOrCreateForm()
    }

    private fun loadOrCreateForm() {
        viewModelScope.launch {
            _uiState.value = IPOS3ggFormUiState.Loading
            if (formId != null) {
                try {
                    val form = oldFormRepository.getIPOS3ggFormById(formId)
                    if (form != null) {
                        _uiState.value = IPOS3ggFormUiState.Editing(
                            form = form,
                            isPatientBirthDateValid = form.patientData.birthDate != null,
                            isCompilationTimestampValid = true // Assuming loaded forms have valid timestamp
                        ).also { validateFormAndUpdateState(it.form) }
                    } else {
                        _uiState.value = IPOS3ggFormUiState.Error("Form non trovato.")
                        // Fallback to new form if not found, or handle error more gracefully
                        initializeNewForm()
                    }
                } catch (e: Exception) {
                    _uiState.value = IPOS3ggFormUiState.Error("Errore nel caricamento del form: ${e.localizedMessage}")
                    initializeNewForm() // Fallback
                }
            } else {
                initializeNewForm()
            }
        }
    }

    private fun initializeNewForm() {
        val user = oldFormRepository.getUserDetails()
        val userBirth = user?.birthday?.split(".")?.let {
            java.time.LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
        }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val newForm = IPOS3ggForm(
            patientData = PatientData(name = user?.name ?: "", surname = user?.surname ?: "", birthDate = userBirth), // Default/mock patient data
            sections = IPOS3ggQuestions.initialSections.map { section ->
                section.copy(questions = section.questions.map { it.copy(score = null, questionText = if (section.sectionId == "Q1" || section.sectionId == "Q2b") "" else it.questionText) })
            }
        )
        _uiState.value = IPOS3ggFormUiState.Editing(form = newForm)
        // Initial validation for a new form might set isFormValid to false
        // if certain fields are mandatory from the start.
        validateFormAndUpdateState(newForm)
    }

    fun onPatientNameChanged(name: String) { // Renamed parameter and method
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(name = name) // Corrected field
            )
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onPatientSurnameChanged(surname: String) { // Renamed parameter and method
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(surname = surname) // Corrected field
            )
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onPatientBirthDateSelected(timestamp: Long?) {
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(birthDate = timestamp)
            )
            _uiState.update {
                currentState.copy(
                    form = updatedForm,
                    isPatientBirthDateValid = timestamp != null
                )
            }
            validateFormAndUpdateState(updatedForm) // Re-validate after explicit state update
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            val updatedForm = currentState.form.copy(compilationTimestamp = timestamp)
            _uiState.update {
                currentState.copy(
                    form = updatedForm,
                    isCompilationTimestampValid = true // Assuming selection makes it valid
                )
            }
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onQuestionResponseChanged(sectionId: String, questionId: Int, newScore: Int?, newText: String? = null) {
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            val updatedSections = currentState.form.sections.map { section ->
                if (section.sectionId == sectionId) {
                    section.copy(questions = section.questions.map { question ->
                        if (question.questionId == questionId) {
                            // For Q1 and Q2b text fields, newText will be used for question.questionText
                            // For Q2b scores and other scales, newScore will be used for question.score
                            val updatedQuestionText = if (sectionId == "Q1" || (sectionId == "Q2b" && newText != null)) {
                                newText ?: question.questionText // Keep existing if newText is null (e.g. only score changed for Q2b)
                            } else {
                                question.questionText // Keep original prompt for scale/choice questions
                            }
                            question.copy(
                                score = newScore ?: question.score, // Keep existing score if newScore is null
                                questionText = updatedQuestionText
                            )
                        } else question
                    })
                } else section
            }
            val updatedForm = currentState.form.copy(sections = updatedSections)
            validateFormAndUpdateState(updatedForm)
        }
    }


    private fun validateFormAndUpdateState(form: IPOS3ggForm) {
        val isPatientNameValid = form.patientData.name.isNotBlank() && form.patientData.surname.isNotBlank() // Corrected fields
        val isBirthDateValid = form.patientData.birthDate != null
        // Assuming compilation timestamp is always valid once set, or could add specific validation
        val isCompilationTimestampValid = true // Simplified for now

        // For IPOS3gg, specific question validation might not be needed beyond ensuring they are filled if required by UI.
        // No complex scoring or inter-question logic.
        // For now, overall form validity depends on patient data.
        val isFormOverallValid = isPatientNameValid && isBirthDateValid && isCompilationTimestampValid

        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            _uiState.value = currentState.copy(
                form = form,
                isFormValid = isFormOverallValid,
                isPatientBirthDateValid = isBirthDateValid,
                isCompilationTimestampValid = isCompilationTimestampValid
                // update other specific validation flags if added
            )
        }
    }

    fun saveForm() {
        (_uiState.value as? IPOS3ggFormUiState.Editing)?.let { currentState ->
            if (!currentState.isFormValid) {
                // Optionally trigger UI feedback about invalid fields
                return
            }
            _uiState.value = currentState.copy(isSaving = true)
            viewModelScope.launch {
                try {
                    val formToSave = currentState.form.copy(lastModified = System.currentTimeMillis())
                    oldFormRepository.saveIPOS3ggForm(formToSave)
                    _uiState.value = IPOS3ggFormUiState.Saved(formToSave)
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(
                        isSaving = false,
                        // Optionally show error message in UI
                        // message = "Errore nel salvataggio: ${e.localizedMessage}"
                    )
                     _uiState.value = IPOS3ggFormUiState.Error("Errore nel salvataggio: ${e.localizedMessage}")
                }
            }
        }
    }
}
