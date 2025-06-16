package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.form.ComidTest
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.ComidQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.COMIDForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.*
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ComidFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ComidFormUiState>(ComidFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<ComidFormUiState> = _uiState.asStateFlow()

    var userId: Int? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect {
                userId = it
            }
        }
    }

    object ValidationKeys {
        const val PATIENT_DATA_SECTION = "patient_data_section" // If patient data needs validation
        const val PATIENT_BIRTH_DATE = "patient_birth_date"
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(order: Int) = "section_order_${order}"
    }

    private fun createNewForm(): COMIDForm {
        val user = userDetailRepository.getCurrentCase()
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
        val patientCode = userDetailRepository.getCurrentCase()?.patientCod ?: return
        _uiState.value = ComidFormUiState.Editing(
            form = createNewForm(),
            isBirthDateValid = true // Assuming static or pre-filled valid date
        )
        viewModelScope.launch {
            formRepository.getComidScaleList(patientCode).results?.firstOrNull { it.iD == formId?.toInt() }?.let { form ->
                updateQuestionResponse(
                    sectionOrder = 1,
                    questionId = 101,
                    response = form.tableARow1
                )
                updateQuestionResponse(
                    sectionOrder = 1,
                    questionId = 102,
                    response = form.tableARow2
                )
                updateQuestionResponse(
                    sectionOrder = 1,
                    questionId = 103,
                    response = form.tableARow3
                )
                updateQuestionResponse(
                    sectionOrder = 1,
                    questionId = 104,
                    response = form.tableARow4
                )
                updateQuestionResponse(
                    sectionOrder = 1,
                    questionId = 105,
                    response = form.tableARow5
                )
                updateQuestionResponse(
                    sectionOrder = 2,
                    questionId = 201,
                    response = form.tableBRow1
                )
                updateQuestionResponse(
                    sectionOrder = 2,
                    questionId = 202,
                    response = form.tableBRow2
                )
                updateQuestionResponse(
                    sectionOrder = 2,
                    questionId = 203,
                    response = form.tableBRow3
                )
                updateQuestionResponse(
                    sectionOrder = 2,
                    questionId = 204,
                    response = form.tableBRow4
                )
                updateQuestionResponse(
                    sectionOrder = 2,
                    questionId = 205,
                    response = form.tableBRow5
                )
                updateQuestionResponse(
                    sectionOrder = 3,
                    questionId = 301,
                    response = form.tableCRow1
                )
                updateQuestionResponse(
                    sectionOrder = 3,
                    questionId = 302,
                    response = form.tableCRow2
                )
                updateQuestionResponse(
                    sectionOrder = 3,
                    questionId = 303,
                    response = form.tableCRow3
                )
                updateQuestionResponse(
                    sectionOrder = 3,
                    questionId = 304,
                    response = form.tableCRow4
                )
                updateQuestionResponse(
                    sectionOrder = 3,
                    questionId = 305,
                    response = form.tableCRow5
                )
                updateQuestionResponse(
                    sectionOrder = 4,
                    questionId = 401,
                    response = form.tableDRow1
                )
                updateQuestionResponse(
                    sectionOrder = 4,
                    questionId = 402,
                    response = form.tableDRow2
                )
                updateQuestionResponse(
                    sectionOrder = 4,
                    questionId = 403,
                    response = form.tableDRow3
                )
                updateQuestionResponse(
                    sectionOrder = 4,
                    questionId = 404,
                    response = form.tableDRow4
                )
                updateQuestionResponse(
                    sectionOrder = 4,
                    questionId = 405,
                    response = form.tableDRow5
                )
                updateQuestionResponse(
                    sectionOrder = 5,
                    questionId = 501,
                    response = form.tableERow1
                )
                updateQuestionResponse(
                    sectionOrder = 5,
                    questionId = 502,
                    response = form.tableERow2
                )
                updateQuestionResponse(
                    sectionOrder = 5,
                    questionId = 503,
                    response = form.tableERow3
                )

                updateQuestionResponse(
                    sectionOrder = 5,
                    questionId = 504,
                    response = form.tableERow4
                )
                updateQuestionResponse(
                    sectionOrder = 5,
                    questionId = 505,
                    response = form.tableERow5
                )
                updateQuestionResponse(
                    sectionOrder = 6,
                    questionId = 601,
                    response = form.tableFRow1
                )
                updateQuestionResponse(
                    sectionOrder = 6,
                    questionId = 602,
                    response = form.tableFRow2
                )
                updateQuestionResponse(
                    sectionOrder = 6,
                    questionId = 603,
                    response = form.tableFRow3
                )
                updateQuestionResponse(
                    sectionOrder = 6,
                    questionId = 604,
                    response = form.tableFRow4
                )
                updateQuestionResponse(
                    sectionOrder = 6,
                    questionId = 605,
                    response = form.tableFRow5
                )
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
            validateFormAndUpdateState(currentState,false)
        }
    }

    private fun validateFormAndUpdateState(editingState: ComidFormUiState.Editing,showError: Boolean): Boolean {
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
        if (showError && newInvalidFieldKeys.isNotEmpty()) {
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

    fun saveForm(formID: String?) {
        val patient = userDetailRepository.getCurrentCase() ?: return
        val patientCod = patient.patientCod ?: return
        val userId = userId ?: return
        val currentState = _uiState.value
        if (currentState !is ComidFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState,true)) {
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
                val data = ComidTest(
                    iD = formID?.toInt(),
                    cODCase = patientCod,
                    iDUser = userId,
                    evalDateTime = Date(formToSave.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                    nameSurnameUser = "",
                    tableARow1 = formToSave.sections[0].questions[0].score == 1,
                    tableARow2 = formToSave.sections[0].questions[1].score == 1,
                    tableARow3 = formToSave.sections[0].questions[2].score == 1,
                    tableARow4 = formToSave.sections[0].questions[3].score == 1,
                    tableARow5 = formToSave.sections[0].questions[4].score == 1,
                    tableBRow1 = formToSave.sections[1].questions[0].score == 1,
                    tableBRow2 = formToSave.sections[1].questions[1].score == 1,
                    tableBRow3 = formToSave.sections[1].questions[2].score == 1,
                    tableBRow4 = formToSave.sections[1].questions[3].score == 1,
                    tableBRow5 = formToSave.sections[1].questions[4].score == 1,
                    tableCRow1 = formToSave.sections[2].questions[0].score == 1,
                    tableCRow2 = formToSave.sections[2].questions[1].score == 1,
                    tableCRow3 = formToSave.sections[2].questions[2].score == 1,
                    tableCRow4 = formToSave.sections[2].questions[3].score == 1,
                    tableCRow5 = formToSave.sections[2].questions[4].score == 1,
                    tableDRow1 = formToSave.sections[3].questions[0].score == 1,
                    tableDRow2 = formToSave.sections[3].questions[1].score == 1,
                    tableDRow3 = formToSave.sections[3].questions[2].score == 1,
                    tableDRow4 = formToSave.sections[3].questions[3].score == 1,
                    tableDRow5 = formToSave.sections[3].questions[4].score == 1,
                    tableERow1 = formToSave.sections[4].questions[0].score == 1,
                    tableERow2 = formToSave.sections[4].questions[1].score == 1,
                    tableERow3 = formToSave.sections[4].questions[2].score == 1,
                    tableERow4 = formToSave.sections[4].questions[3].score == 1,
                    tableERow5 = formToSave.sections[4].questions[4].score == 1,
                    tableFRow1 = formToSave.sections[5].questions[0].score == 1,
                    tableFRow2 = formToSave.sections[5].questions[1].score == 1,
                    tableFRow3 = formToSave.sections[5].questions[2].score == 1,
                    tableFRow4 = formToSave.sections[5].questions[3].score == 1,
                    tableFRow5 = formToSave.sections[5].questions[4].score == 1
                )
                if (formID != null) {
                    formRepository.editComidScale(data)
                } else {
                    formRepository.addComidTestScale(data)
                }
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
