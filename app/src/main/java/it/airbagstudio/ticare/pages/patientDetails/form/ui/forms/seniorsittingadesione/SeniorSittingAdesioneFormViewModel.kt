package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsittingadesione

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingAdesioneQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingAdesioneForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset

class SeniorSittingAdesioneFormViewModel(
    private val formRepository: FormRepository,
    private val formId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeniorSittingAdesioneFormUiState>(SeniorSittingAdesioneFormUiState.Loading)
    val uiState: StateFlow<SeniorSittingAdesioneFormUiState> = _uiState.asStateFlow()

    init {
        loadOrCreateForm()
    }

    private fun loadOrCreateForm() {
        viewModelScope.launch {
            _uiState.value = SeniorSittingAdesioneFormUiState.Loading
            if (formId != null) {
                try {
                    val form = formRepository.getSeniorSittingAdesioneFormById(formId)
                    if (form != null) {
                        _uiState.value = SeniorSittingAdesioneFormUiState.Editing(
                            form = form,
                            isPatientBirthDateValid = form.patientData.birthDate != null, // Assuming PatientData is pre-filled
                            isCompilationTimestampValid = true // Assuming loaded forms have valid timestamp
                        ).also { validateFormAndUpdateState(it.form) }
                    } else {
                        _uiState.value = SeniorSittingAdesioneFormUiState.Error("Form non trovato.")
                        initializeNewForm()
                    }
                } catch (e: Exception) {
                    _uiState.value = SeniorSittingAdesioneFormUiState.Error("Errore nel caricamento del form: ${e.localizedMessage}")
                    initializeNewForm()
                }
            } else {
                initializeNewForm()
            }
        }
    }

    private fun initializeNewForm() {
        // For Senior Sitting, patient data (including zone) is expected to be pre-filled.
        // The ViewModel won't initialize it here but expect it to be passed or loaded.
        // For a new form instance, we'd typically get patient data from a shared source.
        // For this example, we'll assume PatientData() is a placeholder and will be populated.
        val user = formRepository.getUserDetails()
        val userBirth = user?.birthday?.split(".")?.let {
            java.time.LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
        }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val newForm = SeniorSittingAdesioneForm(
            patientData = PatientData(name = user?.name ?: "", surname = user?.surname ?: "", birthDate = userBirth), // This should ideally be pre-populated with name, surname, birthDate, zone
            sections = SeniorSittingAdesioneQuestions.initialSections.map { section ->
                section.copy(questions = section.questions.map {
                    // Scaled questions (1-6) default to null score (no selection)
                    // Open questions (7-8) default to empty text
                    if (section.sectionId == "open_questions") {
                        it.copy(score = null, questionText = "")
                    } else {
                        it.copy(score = null) // Scaled questions start with no score
                    }
                })
            }
        )
        _uiState.value = SeniorSittingAdesioneFormUiState.Editing(form = newForm)
        validateFormAndUpdateState(newForm)
    }

    // Patient data fields (name, surname, birthDate, zone) are read-only for this form.
    // Only compilation timestamp is editable.

    fun onCompilationDateTimeSelected(timestamp: Long) {
        (_uiState.value as? SeniorSittingAdesioneFormUiState.Editing)?.let { currentState ->
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
        (_uiState.value as? SeniorSittingAdesioneFormUiState.Editing)?.let { currentState ->
            val updatedSections = currentState.form.sections.map { section ->
                if (section.sectionId == sectionId) {
                    section.copy(questions = section.questions.map { question ->
                        if (question.questionId == questionId) {
                            question.copy(
                                score = newScore ?: question.score, // For scaled questions
                                questionText = newText ?: question.questionText // For open questions, or keep prompt for scaled
                            )
                        } else question
                    })
                } else section
            }
            val updatedForm = currentState.form.copy(sections = updatedSections)
            validateFormAndUpdateState(updatedForm)
        }
    }

    private fun validateFormAndUpdateState(form: SeniorSittingAdesioneForm) {
        // Patient data (name, surname, birthDate, zone) is considered pre-validated as read-only.
        val isCompilationTimestampValid = true // Assuming it's valid once set.

        // Validate scaled questions (1-6): all must have a score (1-5)
        val scaledQuestionsSection = form.sections.find { it.sectionId == "scaled_questions" }
        // Q1-Q6 are scaled, Q7 is open text in this section
        val allScaledQuestionsAnswered = scaledQuestionsSection?.questions
            ?.filter { it.questionId != SeniorSittingAdesioneQuestions.Q7_AIUTO_SCUDO_ID }
            ?.all { it.score != null && it.score in 1..5 } ?: false

        // Validate open question Q7 (now in scaled_questions section)
        val q7Response = scaledQuestionsSection?.questions?.find { it.questionId == SeniorSittingAdesioneQuestions.Q7_AIUTO_SCUDO_ID }
        val isQ7Answered = q7Response?.questionText?.isNotBlank() ?: false

        // Validate open question Q8 (in its own section)
        val managementSection = form.sections.find { it.sectionId == "management_organization_questions" }
        val q8Response = managementSection?.questions?.find { it.questionId == SeniorSittingAdesioneQuestions.Q8_SUGGERIMENTI_ID }
        val isQ8Answered = q8Response?.questionText?.isNotBlank() ?: false

        val isFormOverallValid = isCompilationTimestampValid && allScaledQuestionsAnswered && isQ7Answered && isQ8Answered

        (_uiState.value as? SeniorSittingAdesioneFormUiState.Editing)?.let { currentState ->
            _uiState.value = currentState.copy(
                form = form,
                isFormValid = isFormOverallValid,
                isCompilationTimestampValid = isCompilationTimestampValid
                // isPatientBirthDateValid is true as it's pre-filled
            )
        }
    }

    fun saveForm() {
        (_uiState.value as? SeniorSittingAdesioneFormUiState.Editing)?.let { currentState ->
            if (!currentState.isFormValid) {
                // Optionally trigger UI feedback about invalid fields
                // For example, by setting error states in UiState for specific questions
                return
            }
            _uiState.value = currentState.copy(isSaving = true)
            viewModelScope.launch {
                try {
                    val formToSave = currentState.form.copy(lastModified = System.currentTimeMillis())
                    formRepository.saveSeniorSittingAdesioneForm(formToSave)
                    _uiState.value = SeniorSittingAdesioneFormUiState.Saved(formToSave)
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(
                        isSaving = false,
                    )
                    _uiState.value = SeniorSittingAdesioneFormUiState.Error("Errore nel salvataggio: ${e.localizedMessage}")
                }
            }
        }
    }
}
