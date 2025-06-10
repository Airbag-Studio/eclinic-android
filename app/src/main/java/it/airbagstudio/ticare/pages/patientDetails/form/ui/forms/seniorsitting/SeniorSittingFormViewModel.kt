package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingType
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SeniorSittingFormViewModel(
    private val formRepository: FormRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeniorSittingFormUiState>(SeniorSittingFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<SeniorSittingFormUiState> = _uiState.asStateFlow()

    object ValidationKeys {
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(sectionId: String) = "section_${sectionId}"
    }

    private fun createNewForm(): SeniorSittingForm {
        val user = formRepository.getUserDetails()
        val userBirth = user?.birthday?.split(".")?.let {
            try {
                val day = it[0].toInt()
                val month = it[1].toInt()
                val year = it[2].toInt()
                java.time.LocalDate.of(year, month, day)
                    .atStartOfDay(java.time.ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()
            } catch (e: Exception) {
                null
            }
        }
        
        return SeniorSittingForm(
            patientData = PatientData(
                name = user?.name ?: "",
                surname = user?.surname ?: "",
                birthDate = userBirth,
                zone = "" // Zone field might not be available in user details
            ),
            type = SeniorSittingType.ADESIONE, // Default to Adesione
            sections = SeniorSittingQuestions.getInitialSections(SeniorSittingType.ADESIONE),
            compilationTimestamp = System.currentTimeMillis()
        )
    }

    fun initForm(formId: String?) {
        if (formId == null) {
            _uiState.value = SeniorSittingFormUiState.Editing(
                form = createNewForm()
            )
        } else {
            viewModelScope.launch {
                _uiState.value = SeniorSittingFormUiState.Loading
                try {
                    // TODO: Implement form loading from repository
                    // For now, create a new form
                    _uiState.value = SeniorSittingFormUiState.Editing(
                        form = createNewForm()
                    )
                } catch (e: IOException) {
                    _uiState.value = SeniorSittingFormUiState.Error("Errore nel caricamento del form Senior Sitting: ${e.message}")
                } catch (e: Exception) {
                    _uiState.value = SeniorSittingFormUiState.Error("Errore imprevisto nel caricamento del form Senior Sitting: ${e.message}")
                }
            }
        }
    }

    fun onTypeChanged(newType: SeniorSittingType) {
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val updatedForm = currentState.form.copy(
                type = newType,
                sections = SeniorSittingQuestions.getInitialSections(newType),
                formType = newType.typeName
            )
            _uiState.update {
                currentState.copy(form = updatedForm)
            }
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(compilationTimestamp = timestamp)
                )
            }
        }
    }

    fun onQuestionResponseChanged(sectionId: String, questionId: Int, newScore: Int?, newText: String?) {
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val sectionIndex = currentState.form.sections.indexOfFirst { it.sectionId == sectionId }
            if (sectionIndex == -1) return

            val updatedSections = currentState.form.sections.toMutableList()
            val currentSection = updatedSections[sectionIndex]

            val questionIndex = currentSection.questions.indexOfFirst { it.questionId == questionId }
            if (questionIndex == -1) return

            val updatedQuestions = currentSection.questions.toMutableList()
            val currentQuestion = updatedQuestions[questionIndex]
            
            updatedQuestions[questionIndex] = currentQuestion.copy(
                score = newScore,
                questionText = newText ?: currentQuestion.questionText
            )

            updatedSections[sectionIndex] = currentSection.copy(questions = updatedQuestions)

            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(sections = updatedSections)
                )
            }
        }
    }

    fun onCheckboxChanged(sectionOrder: Int, questionId: Int, isChecked: Boolean) {
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val sectionIndex = currentState.form.sections.indexOfFirst { it.order == sectionOrder }
            if (sectionIndex == -1) return

            val updatedSections = currentState.form.sections.toMutableList()
            val currentSection = updatedSections[sectionIndex]

            val questionIndex = currentSection.questions.indexOfFirst { it.questionId == questionId }
            if (questionIndex == -1) return

            val updatedQuestions = currentSection.questions.toMutableList()
            val currentQuestion = updatedQuestions[questionIndex]
            
            updatedQuestions[questionIndex] = currentQuestion.copy(
                score = if (isChecked) 1 else 0
            )

            updatedSections[sectionIndex] = currentSection.copy(questions = updatedQuestions)

            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(sections = updatedSections)
                )
            }
        }
    }

    private fun validateFormAndUpdateState(editingState: SeniorSittingFormUiState.Editing): Boolean {
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()

        when (editingState.form.type) {
            SeniorSittingType.ADESIONE -> {
                // Validate Adesione form
                editingState.form.sections.forEach { section ->
                    when (section.sectionId) {
                        "scaled_questions" -> {
                            // Check that all scale questions (1-6) have scores, Q7 can be empty text
                            val scaleQuestions = section.questions.filter { 
                                it.questionId != SeniorSittingQuestions.Q7_AIUTO_SCUDO_ID 
                            }
                            if (scaleQuestions.any { it.score == null }) {
                                newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                                isValid = false
                            }
                        }
                        "management_organization_questions" -> {
                            // Q8 text field can be empty - no validation needed
                        }
                    }
                }
            }
            SeniorSittingType.NON_ADESIONE -> {
                // Validate Non-Adesione form
                editingState.form.sections.forEach { section ->
                    when (section.sectionId) {
                        "non_adesione_reasons" -> {
                            // At least one checkbox must be selected
                            if (section.questions.none { it.score == 1 }) {
                                newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                                isValid = false
                            }
                        }
                        "non_adesione_suggestions" -> {
                            // Suggestions field can be empty - no validation needed
                        }
                    }
                }
            }
        }

        if (newInvalidFieldKeys.isNotEmpty()) {
            validationErrors.add("Si prega di completare tutte le sezioni richieste prima di salvare.")
        }

        _uiState.update {
            (it as SeniorSittingFormUiState.Editing).copy(
                isFormValid = isValid,
                validationErrors = validationErrors,
                invalidFieldKeys = newInvalidFieldKeys
            )
        }
        return isValid
    }

    fun saveForm() {
        val currentState = _uiState.value
        if (currentState !is SeniorSittingFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            val formToSave = currentState.form.copy(
                lastModified = System.currentTimeMillis()
            )

            try {
                // TODO: Implement form saving to repository
                // For now, just simulate success
                kotlinx.coroutines.delay(1000) // Simulate network delay
                _uiState.value = SeniorSittingFormUiState.Saved(formToSave)
            } catch (e: IOException) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore nel salvataggio del form: ${e.message}")
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore imprevisto nel salvataggio del form: ${e.message}")
                )
            }
        }
    }
}