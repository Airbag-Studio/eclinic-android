package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos7gg // Package name changed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOS7ggQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOS7ggForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IPOS7ggFormViewModel( // Class name changed
    private val formRepository: FormRepository,
    private val formId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow<IPOS7ggFormUiState>(IPOS7ggFormUiState.Loading) // UI State type changed
    val uiState: StateFlow<IPOS7ggFormUiState> = _uiState.asStateFlow() // UI State type changed

    init {
        loadOrCreateForm()
    }

    private fun loadOrCreateForm() {
        viewModelScope.launch {
            _uiState.value = IPOS7ggFormUiState.Loading // UI State type changed
            if (formId != null) {
                try {
                    val form = formRepository.getIPOS7ggFormById(formId) // Repository call changed
                    if (form != null) {
                        _uiState.value = IPOS7ggFormUiState.Editing( // UI State type changed
                            form = form,
                            isPatientBirthDateValid = form.patientData.birthDate != null,
                            isCompilationTimestampValid = true
                        ).also { validateFormAndUpdateState(it.form) }
                    } else {
                        _uiState.value = IPOS7ggFormUiState.Error("Form non trovato.") // UI State type changed
                        initializeNewForm()
                    }
                } catch (e: Exception) {
                    _uiState.value = IPOS7ggFormUiState.Error("Errore nel caricamento del form: ${e.localizedMessage}") // UI State type changed
                    initializeNewForm()
                }
            } else {
                initializeNewForm()
            }
        }
    }

    private fun initializeNewForm() {
        val newForm = IPOS7ggForm( // Model type changed
            patientData = PatientData(),
            sections = IPOS7ggQuestions.initialSections.map { section -> // Questions object changed
                section.copy(questions = section.questions.map { it.copy(score = null, questionText = if (section.sectionId == "Q1" || section.sectionId == "Q2b") "" else it.questionText) })
            }
        )
        _uiState.value = IPOS7ggFormUiState.Editing(form = newForm) // UI State type changed
        validateFormAndUpdateState(newForm)
    }

    fun onPatientNameChanged(name: String) {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(name = name)
            )
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onPatientSurnameChanged(surname: String) {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(surname = surname)
            )
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onPatientBirthDateSelected(timestamp: Long?) {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            val updatedForm = currentState.form.copy(
                patientData = currentState.form.patientData.copy(birthDate = timestamp)
            )
            _uiState.update {
                currentState.copy(
                    form = updatedForm,
                    isPatientBirthDateValid = timestamp != null
                )
            }
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            val updatedForm = currentState.form.copy(compilationTimestamp = timestamp)
            _uiState.update {
                currentState.copy(
                    form = updatedForm,
                    isCompilationTimestampValid = true
                )
            }
            validateFormAndUpdateState(updatedForm)
        }
    }

    fun onQuestionResponseChanged(sectionId: String, questionId: Int, newScore: Int?, newText: String? = null) {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            val updatedSections = currentState.form.sections.map { section ->
                if (section.sectionId == sectionId) {
                    section.copy(questions = section.questions.map { question ->
                        if (question.questionId == questionId) {
                            val updatedQuestionText = if (sectionId == "Q1" || (sectionId == "Q2b" && newText != null)) {
                                newText ?: question.questionText
                            } else {
                                question.questionText
                            }
                            question.copy(
                                score = newScore ?: question.score,
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


    private fun validateFormAndUpdateState(form: IPOS7ggForm) { // Model type changed
        val isPatientNameValid = form.patientData.name.isNotBlank() && form.patientData.surname.isNotBlank()
        val isBirthDateValid = form.patientData.birthDate != null
        val isCompilationTimestampValid = true

        val isFormOverallValid = isPatientNameValid && isBirthDateValid && isCompilationTimestampValid

        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            _uiState.value = currentState.copy(
                form = form,
                isFormValid = isFormOverallValid,
                isPatientBirthDateValid = isBirthDateValid,
                isCompilationTimestampValid = isCompilationTimestampValid
            )
        }
    }

    fun saveForm() {
        (_uiState.value as? IPOS7ggFormUiState.Editing)?.let { currentState -> // UI State type changed
            if (!currentState.isFormValid) {
                return
            }
            _uiState.value = currentState.copy(isSaving = true)
            viewModelScope.launch {
                try {
                    val formToSave = currentState.form.copy(lastModified = System.currentTimeMillis())
                    formRepository.saveIPOS7ggForm(formToSave) // Repository call changed
                    _uiState.value = IPOS7ggFormUiState.Saved(formToSave) // UI State type changed
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(
                        isSaving = false,
                    )
                     _uiState.value = IPOS7ggFormUiState.Error("Errore nel salvataggio: ${e.localizedMessage}") // UI State type changed
                }
            }
        }
    }
}
