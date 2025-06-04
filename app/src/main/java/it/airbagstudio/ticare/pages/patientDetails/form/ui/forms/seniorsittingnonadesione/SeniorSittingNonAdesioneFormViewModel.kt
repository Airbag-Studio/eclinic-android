package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingnonadesione

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingNonAdesioneQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingNonAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SeniorSittingNonAdesioneFormViewModel(
    private val formId: String?,
    private val formRepository: FormRepository,
    private val patientData: PatientData // Assuming patient data is passed upon creation/navigation
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeniorSittingNonAdesioneFormUiState>(SeniorSittingNonAdesioneFormUiState.Loading)
    val uiState: StateFlow<SeniorSittingNonAdesioneFormUiState> = _uiState.asStateFlow()

    init {
        loadForm()
    }

    private fun loadForm() {
        viewModelScope.launch {
            _uiState.value = SeniorSittingNonAdesioneFormUiState.Loading
            if (formId == null) {
                // New form
                _uiState.value = SeniorSittingNonAdesioneFormUiState.Editing(
                    formId = null,
                    patientData = patientData,
                    compilationTimestamp = System.currentTimeMillis(),
                    sections = SeniorSittingNonAdesioneQuestions.sections.map { section ->
                        section.copy(questions = section.questions.map { it.copy(score = 0) }) // Initialize checkbox scores to 0
                    },
                    isSaving = false
                )
            } else {
                // Existing form
                try {
                    val form = formRepository.getSeniorSittingNonAdesioneFormById(formId)
                    if (form != null) {
                        _uiState.value = SeniorSittingNonAdesioneFormUiState.Editing(
                            formId = form.id,
                            patientData = form.patientData,
                            compilationTimestamp = form.compilationTimestamp,
                            sections = form.sections,
                            isSaving = false
                        )
                    } else {
                        _uiState.value = SeniorSittingNonAdesioneFormUiState.Error("Form non trovato")
                    }
                } catch (e: Exception) {
                    _uiState.value = SeniorSittingNonAdesioneFormUiState.Error("Errore nel caricamento del form: ${e.message}")
                }
            }
        }
    }

    fun onCheckboxChanged(sectionOrder: Int, questionId: Int, isChecked: Boolean) {
        _uiState.update { currentState ->
            if (currentState is SeniorSittingNonAdesioneFormUiState.Editing) {
                val updatedSections = currentState.sections.map { section ->
                    if (section.order == sectionOrder) {
                        section.copy(questions = section.questions.map { question ->
                            if (question.questionId == questionId) {
                                question.copy(score = if (isChecked) 1 else 0)
                            } else question
                        })
                    } else section
                }
                currentState.copy(sections = updatedSections)
            } else currentState
        }
        // Trigger validation after state update
        validateFormAndUpdateState()
    }

    fun onOpenQuestionChanged(sectionOrder: Int, questionId: Int, text: String) {
        _uiState.update { currentState ->
            if (currentState is SeniorSittingNonAdesioneFormUiState.Editing) {
                val updatedSections = currentState.sections.map { section ->
                    if (section.order == sectionOrder) {
                        section.copy(questions = section.questions.map { question ->
                            if (question.questionId == questionId) {
                                question.copy(questionText = text) // Store text in questionText for open questions
                            } else question
                        })
                    } else section
                }
                currentState.copy(sections = updatedSections)
            } else currentState
        }
        // Trigger validation after state update
        validateFormAndUpdateState()
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        _uiState.update { currentState ->
            if (currentState is SeniorSittingNonAdesioneFormUiState.Editing) {
                currentState.copy(compilationTimestamp = timestamp)
            } else currentState
        }
    }

    private fun validateFormAndUpdateState(): Boolean {
        if (_uiState.value is SeniorSittingNonAdesioneFormUiState.Editing) {
            val editingState = _uiState.value as SeniorSittingNonAdesioneFormUiState.Editing

            // Section 1: At least one checkbox selected
            val section1 = editingState.sections.find { it.order == 1 }
            val isSection1Valid = section1?.questions?.any { it.score == 1 } ?: false

            // Section 2: Open question text is not blank
            val section2 = editingState.sections.find { it.order == 2 }
            val openQuestionResponse = section2?.questions?.find { it.questionId == 10 } // Assuming ID 10 for open question
            val isSection2Valid = !openQuestionResponse?.questionText.isNullOrBlank()


            _uiState.update {
                if (it is SeniorSittingNonAdesioneFormUiState.Editing) {
                    it.copy(
                        isSection1Valid = isSection1Valid,
                        isSection2Valid = isSection2Valid,
                        generalError = if (isSection1Valid && isSection2Valid) null else "Completare tutti i campi obbligatori."
                    )
                } else it
            }
            return isSection1Valid && isSection2Valid
        }
        return false
    }


    fun saveForm() {
        if (!validateFormAndUpdateState()) {
            return // Validation failed, do not proceed with saving
        }

        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is SeniorSittingNonAdesioneFormUiState.Editing) {
                _uiState.value = currentState.copy(isSaving = true, generalError = null)
                try {
                    val formToSave = SeniorSittingNonAdesioneForm(
                        id = currentState.formId ?: UUID.randomUUID().toString(),
                        patientData = currentState.patientData,
                        compilationTimestamp = currentState.compilationTimestamp,
                        sections = currentState.sections,
                        creationDate = if (currentState.formId == null) System.currentTimeMillis() else currentState.sections.firstOrNull()?.let { sec -> currentState.formId?.let { id -> formRepository.getSeniorSittingNonAdesioneFormById(id)?.creationDate } } ?: System.currentTimeMillis(),
                        lastModified = System.currentTimeMillis()
                    )
                    formRepository.saveSeniorSittingNonAdesioneForm(formToSave)
                    _uiState.value = SeniorSittingNonAdesioneFormUiState.Saved(formToSave)
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(isSaving = false, generalError = "Errore nel salvataggio: ${e.message}")
                }
            }
        }
    }
}
