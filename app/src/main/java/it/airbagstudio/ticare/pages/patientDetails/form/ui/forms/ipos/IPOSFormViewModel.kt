package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSTimePeriod
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class IPOSFormViewModel(
    private val formRepository: FormRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IPOSFormUiState>(IPOSFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<IPOSFormUiState> = _uiState.asStateFlow()

    object ValidationKeys {
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(sectionId: String) = "section_${sectionId}"
    }

    private fun createNewForm(): IPOSForm {
        val user = formRepository.getUserDetails()
        val userBirth = user?.birthday?.split(".")?.let {
            // Handle birthday parsing similar to other forms
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
        
        return IPOSForm(
            patientData = PatientData(
                name = user?.name ?: "",
                surname = user?.surname ?: "",
                birthDate = userBirth
            ),
            timePeriod = IPOSTimePeriod.DAYS_3, // Default to 3 days
            sections = IPOSQuestions.getInitialSections(IPOSTimePeriod.DAYS_3),
            compilationTimestamp = System.currentTimeMillis()
        )
    }

    fun initForm(formId: String?) {
        if (formId == null) {
            _uiState.value = IPOSFormUiState.Editing(
                form = createNewForm()
            )
        } else {
            viewModelScope.launch {
                _uiState.value = IPOSFormUiState.Loading
                try {
                    // TODO: Implement form loading from repository
                    // For now, create a new form
                    _uiState.value = IPOSFormUiState.Editing(
                        form = createNewForm()
                    )
                } catch (e: IOException) {
                    _uiState.value = IPOSFormUiState.Error("Errore nel caricamento del form IPOS: ${e.message}")
                } catch (e: Exception) {
                    _uiState.value = IPOSFormUiState.Error("Errore imprevisto nel caricamento del form IPOS: ${e.message}")
                }
            }
        }
    }

    fun onTimePeriodChanged(newPeriod: IPOSTimePeriod) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
            val updatedForm = currentState.form.copy(
                timePeriod = newPeriod,
                sections = IPOSQuestions.getInitialSections(newPeriod)
            )
            _uiState.update {
                currentState.copy(form = updatedForm)
            }
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(compilationTimestamp = timestamp)
                )
            }
        }
    }

    fun onQuestionResponseChanged(sectionId: String, questionId: Int, newScore: Int?, newText: String?) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
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

    private fun validateFormAndUpdateState(editingState: IPOSFormUiState.Editing): Boolean {
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()

        // Validate all questions in all sections are answered
        editingState.form.sections.forEach { section ->
            when (section.sectionId) {
                "Q1" -> {
                    // Q1 requires at least one non-empty concern
                    if (section.questions.all { it.questionText.isBlank() }) {
                        newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                        isValid = false
                    }
                }
                "Q2" -> {
                    // Q2 requires all symptoms to be rated
                    if (section.questions.any { it.score == null }) {
                        newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                        isValid = false
                    }
                }
                "Q2b" -> {
                    // Q2b requires that if text is provided, score must also be provided
                    section.questions.forEach { question ->
                        if (question.questionText.isNotBlank() && question.score == null) {
                            newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                            isValid = false
                        }
                    }
                }
                "Q3_Q9" -> {
                    // Q3-Q9 requires all questions to be answered
                    if (section.questions.any { it.score == null }) {
                        newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                        isValid = false
                    }
                }
                "Q10" -> {
                    // Q10 requires a selection
                    if (section.questions.any { it.score == null }) {
                        newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                        isValid = false
                    }
                }
            }
        }

        if (newInvalidFieldKeys.isNotEmpty()) {
            validationErrors.add("Si prega di completare tutte le sezioni richieste prima di salvare.")
        }

        _uiState.update {
            (it as IPOSFormUiState.Editing).copy(
                isFormValid = isValid,
                validationErrors = validationErrors,
                invalidFieldKeys = newInvalidFieldKeys
            )
        }
        return isValid
    }

    fun saveForm() {
        val currentState = _uiState.value
        if (currentState !is IPOSFormUiState.Editing) return

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
                _uiState.value = IPOSFormUiState.Saved(formToSave)
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