package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.extensions.parseServerDateStringFormat
import ch.ticare.eclinic.library.extensions.parseServerDateTimeStringFormat
import ch.ticare.eclinic.library.extensions.parseServerDateTimeUSStringFormat
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.ComidQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.io.IOException
import java.time.*
import java.util.Calendar

class ComidFormViewModel(
    private val formRepository: FormRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ComidFormUiState>(ComidFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<ComidFormUiState> = _uiState.asStateFlow()

    object ValidationKeys {
        const val PATIENT_DATA_SECTION = "patient_data_section" // If patient data needs validation
        const val PATIENT_BIRTH_DATE = "patient_birth_date"
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(order: Int) = "section_order_${order}"
    }

    private fun createNewForm(): COMIDForm {
        val user = formRepository.getUserDetails()
        val userBirth = user?.birthday?.split(".")?.let {
            LocalDate.of(it[2].toInt(), it[1].toInt(), it[0].toInt())
        }?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        return COMIDForm(
            patientData = PatientData(name = user?.name ?: "", surname = user?.surname ?: "", birthDate = userBirth),
            sections = ComidQuestions.getInitialSections(),
            compilationTimestamp = System.currentTimeMillis()
        )
    }

    fun initForm(formId: String?) {
        if (formId == null) {
            _uiState.value = ComidFormUiState.Editing(
                form = createNewForm(),
                isBirthDateValid = true // Assuming static or pre-filled valid date
            )
        } else {
            viewModelScope.launch {
                _uiState.value = ComidFormUiState.Loading
                try {
                    val form = formRepository.getComidFormById(formId)
                    if (form != null) {
                        _uiState.value = ComidFormUiState.Editing(
                            form = form,
                            isFormValid = true, // Assume loaded form is valid initially
                            isBirthDateValid = form.patientData.birthDate != null
                        )
                    } else {
                        _uiState.value = ComidFormUiState.Error("Form COMID non trovato.")
                    }
                } catch (e: IOException) {
                    _uiState.value = ComidFormUiState.Error("Errore nel caricamento del form COMID: ${e.message}")
                } catch (e: Exception) {
                    _uiState.value = ComidFormUiState.Error("Errore imprevisto nel caricamento del form COMID: ${e.message}")
                }
            }
        }
    }

    fun onBirthDateSelected(timestamp: Long?) { // Allow null for clearing or invalid state
        val currentState = _uiState.value
        if (currentState is ComidFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(
                        patientData = currentState.form.patientData.copy(birthDate = timestamp)
                    ),
                    patientData = currentState.form.patientData.copy(birthDate = timestamp), // also update convenience property
                    isBirthDateValid = timestamp != null // Basic validation: must be selected
                )
            }
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is ComidFormUiState.Editing) {
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(compilationTimestamp = timestamp)
                )
            }
        }
    }
    
    fun updateQuestionResponse(sectionOrder: Int, questionId: Int, response: Boolean) { // true for "yes", false for "no"
        val currentState = _uiState.value
        if (currentState is ComidFormUiState.Editing) {
            val score = if (response) 1 else 0 // Map boolean to 0 or 1

            val sectionIndex = currentState.form.sections.indexOfFirst { it.order == sectionOrder }
            if (sectionIndex == -1) return

            val updatedSections = currentState.form.sections.toMutableList()
            val currentSection = updatedSections[sectionIndex]

            val questionIndex = currentSection.questions.indexOfFirst { it.questionId == questionId }
            if (questionIndex == -1) return

            val updatedQuestions = currentSection.questions.toMutableList()
            updatedQuestions[questionIndex] = updatedQuestions[questionIndex].copy(score = score)

            updatedSections[sectionIndex] = currentSection.copy(questions = updatedQuestions)

            _uiState.update {
                (it as ComidFormUiState.Editing).copy(
                    form = it.form.copy(sections = updatedSections)
                )
            }
        }
    }

    private fun validateFormAndUpdateState(editingState: ComidFormUiState.Editing): Boolean {
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()
        var isBirthDateStillValid = editingState.isBirthDateValid // Preserve current state unless changed

        // Validate Patient Birth Date
        if (editingState.form.patientData.birthDate == null) {
            isBirthDateStillValid = false
            newInvalidFieldKeys.add(ValidationKeys.PATIENT_BIRTH_DATE)
            isValid = false
        } else {
            isBirthDateStillValid = true // It's provided
        }
        
        // Validate Compilation Timestamp (assuming it's mandatory)
        // For simplicity, we assume if it's the default System.currentTimeMillis() or user selected, it's fine.
        // More complex validation (e.g., not in future) could be added.
        // if (editingState.form.compilationTimestamp == 0L) { // Example check
        //    newInvalidFieldKeys.add(ValidationKeys.COMPILATION_TIMESTAMP)
        //    isValid = false
        // }

        // Validate all questions in all sections are answered (score must not be null)
        editingState.form.sections.forEach { section ->
            if (section.questions.any { it.score == null }) { // Check for unanswered questions
                newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.order))
                isValid = false
            }
        }
        
        // Add validation error message if there are unanswered questions
        if (newInvalidFieldKeys.isNotEmpty()) {
            validationErrors.add("Si prega di rispondere a tutte le domande prima di salvare.")
        }
        
        _uiState.update {
            (it as ComidFormUiState.Editing).copy(
                isFormValid = isValid,
                isBirthDateValid = isBirthDateStillValid,
                validationErrors = validationErrors,
                invalidFieldKeys = newInvalidFieldKeys
            )
        }
        return isValid
    }

    fun saveForm() {
        val currentState = _uiState.value
        if (currentState !is ComidFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState)) {
            // Optionally, set a general error message if needed, or rely on field highlights
            // _uiState.update { currentState.copy(validationErrors = listOf("Please fill all required fields.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            val formToSave = currentState.form.copy(
                lastModified = System.currentTimeMillis()
                // creationDate is handled by COMIDForm default or loaded for existing
            )

            try {
                formRepository.saveComidForm(formToSave)
                _uiState.value = ComidFormUiState.Saved(formToSave) // Transition to Saved state
            } catch (e: IOException) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    // Update with error message to display
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    // Update with error message to display
                )
            }
        }
    }
}
