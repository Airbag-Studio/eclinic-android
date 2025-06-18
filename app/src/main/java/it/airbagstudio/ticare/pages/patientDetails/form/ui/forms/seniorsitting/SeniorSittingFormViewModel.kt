package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.Contact
import ch.ticare.eclinic.library.entity.SeniorSittingScalePost
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.SeniorSittingQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SeniorSittingType
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class SeniorSittingFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeniorSittingFormUiState>(SeniorSittingFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<SeniorSittingFormUiState> = _uiState.asStateFlow()

    private var userId: Int? = null

    object ValidationKeys {
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(sectionId: String) = "section_${sectionId}"
    }

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect(){
                userId = it
            }
        }

    }

    private fun createNewForm(): SeniorSittingForm {
        val user = userDetailRepository.getCurrentCase()
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
        var case = userDetailRepository.getCurrentCase() ?: return
        val caseCode = case.patientCod ?: return
        val contacts = userDetailRepository.getCurrentCase()?.contacts?.otherContacts ?: listOf()
        _uiState.value = SeniorSittingFormUiState.Editing(
            form = createNewForm()
        )
        viewModelScope.launch {
            formRepository.getSeniorSittingScaleList(caseCode).results?.firstOrNull { it.iD == formId?.toInt() }?.let { form ->


                val isAdesione = form.modality
                onTypeChanged(if (isAdesione) SeniorSittingType.ADESIONE else SeniorSittingType.NON_ADESIONE)
                val currentState = _uiState.value
                if (currentState is SeniorSittingFormUiState.Editing) {
                    if (isAdesione){
                        val updatedForm = currentState.form.copy(
                            selectedCaregiver = contacts.firstOrNull { it.id == form.iDContact },
                            compilationTimestamp = System.currentTimeMillis(),
                        )
                        _uiState.update {
                            currentState.copy(form = updatedForm)
                        }
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q1_GRADIMENTO_ID,
                            newScore = form.tableARow1,
                            newText = null
                        )
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q2_UTILITA_SERVIZIO_ID,
                            newScore = form.tableARow2,
                            newText = null)
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q3_QUALITA_OFFERTA_ID,
                            newScore = form.tableARow3,
                            newText = null)
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q4_UTILITA_SGRAVO_CAREGIVER_ID,
                            newScore = form.tableARow4,
                            newText = null)
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q5_UTILITA_CONTATTI_SOCIALI_ID,
                            newScore = form.tableARow5,
                            newText = null)
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q6_INCIDENZA_COSTO_ID,
                            newScore = form.tableARow6,
                            newText = null)
                        onQuestionResponseChanged(
                            sectionId = "scaled_questions",
                            questionId = SeniorSittingQuestions.Q7_AIUTO_SCUDO_ID,
                            newScore = null,
                            newText = form.tableARow7 ?: ""
                        )
                        onQuestionResponseChanged(
                            sectionId = "management_organization_questions",
                            questionId = SeniorSittingQuestions.Q8_SUGGERIMENTI_ID,
                            newScore = null,
                            newText = form.tableARow8 ?: ""
                        )
                    } else {
                        val updatedForm = currentState.form.copy(
                            selectedCaregiver = contacts.firstOrNull { it.id == form.iDContact },
                            compilationTimestamp = System.currentTimeMillis(),
                        )
                        _uiState.update {
                            currentState.copy(form = updatedForm)
                        }
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q1_AUTONOMIA_ID,
                            isChecked = form.tableBRow1 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q2_ALTRI_AIUTI_ID,
                            isChecked = form.tableBRow2 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q3_NON_PERTINENTE_ID,
                            isChecked = form.tableBRow3 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q4_COSTO_SOSTENERE_ID,
                            isChecked = form.tableBRow4 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q5_COSTO_CARO_ID,
                            isChecked = form.tableBRow5 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q6_NON_SUFFICIENTE_ID,
                            isChecked = form.tableBRow6 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q7_FASCIA_ORARIA_ID,
                            isChecked = form.tableBRow7 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q8_COMPETENZA_ID,
                            isChecked = form.tableBRow8 == 1
                        )
                        onCheckboxChanged(
                            sectionOrder = 1,
                            questionId = SeniorSittingQuestions.NA_Q9_NON_RISPONDE_ID,
                            isChecked = form.tableBRow9 == 1
                        )
                        onQuestionResponseChanged(
                            sectionId = "non_adesione_suggestions",
                            questionId = SeniorSittingQuestions.NA_Q10_SUGGERIMENTI_ID,
                            newScore = null,
                            newText = form.tableBRow10 ?: ""

                        )
                    }
                }

            }

        }
    }

    fun  selectCaregiver(contact: Contact){
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val updatedForm = currentState.form.copy(
                selectedCaregiver = contact
            )
            val updatedState = currentState.copy(form = updatedForm)
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
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
            val updatedState = currentState.copy(form = updatedForm)
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val updatedState = currentState.copy(
                form = currentState.form.copy(compilationTimestamp = timestamp)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
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

            val updatedState = currentState.copy(
                form = currentState.form.copy(sections = updatedSections)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
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

            val updatedState = currentState.copy(
                form = currentState.form.copy(sections = updatedSections)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }

    }

    private fun validateFormAndUpdateState(editingState: SeniorSittingFormUiState.Editing): Boolean {
        Log.d("SeniorSittingForm", "=== VALIDATION STARTED ===")
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()
        val selectedCaregiver = editingState.form.selectedCaregiver ?: run {
            Log.e("SeniorSittingForm", "No caregiver selected!")
            return false
        }
        Log.d("SeniorSittingForm", "Caregiver validated: ${selectedCaregiver.fullname}")
        when (editingState.form.type) {
            SeniorSittingType.ADESIONE -> {
                Log.d("SeniorSittingForm", "Validating ADESIONE form")
                // Validate Adesione form
                editingState.form.sections.forEach { section ->
                    Log.d("SeniorSittingForm", "Validating section: ${section.sectionId}")
                    when (section.sectionId) {
                        "scaled_questions" -> {
                            // Check that all scale questions (1-6) have scores, Q7 can be empty text
                            val scaleQuestions = section.questions.filter { 
                                it.questionId != SeniorSittingQuestions.Q7_AIUTO_SCUDO_ID 
                            }
                            Log.d("SeniorSittingForm", "Scale questions (excluding Q7): ${scaleQuestions.size}")
                            scaleQuestions.forEach { q ->
                                Log.d("SeniorSittingForm", "  Q${q.questionId}: score=${q.score}")
                            }
                            val invalidQuestions = scaleQuestions.filter { it.score == null }
                            if (invalidQuestions.isNotEmpty()) {
                                Log.e("SeniorSittingForm", "Invalid scale questions: ${invalidQuestions.map { it.questionId }}")
                                newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                                isValid = false
                            } else {
                                Log.d("SeniorSittingForm", "All scale questions are valid")
                            }
                        }
                        "management_organization_questions" -> {
                            Log.d("SeniorSittingForm", "Q8 text field - no validation needed")
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
            Log.e("SeniorSittingForm", "Validation failed - invalid fields: $newInvalidFieldKeys")
            validationErrors.add("Si prega di completare tutte le sezioni richieste prima di salvare.")
        } else {
            Log.d("SeniorSittingForm", "Validation passed - form is valid")
        }

        _uiState.update {
            (it as SeniorSittingFormUiState.Editing).copy(
                isFormValid = isValid,
                validationErrors = validationErrors,
                invalidFieldKeys = newInvalidFieldKeys
            )
        }
        Log.d("SeniorSittingForm", "=== VALIDATION COMPLETED: isValid=$isValid ===")
        return isValid
    }

    fun saveForm(formId: String?) {
        Log.d("SeniorSittingForm", "=== SAVE FORM STARTED ===")
        val currentState = _uiState.value
        val case = userDetailRepository.getCurrentCase() ?: return
        val userId = userId ?: return
        val selectedCaregiver = (currentState as? SeniorSittingFormUiState.Editing)?.form?.selectedCaregiver ?: return

        val isAdesione = currentState.form.type == SeniorSittingType.ADESIONE
        Log.d("SeniorSittingForm", "Form type: ${if (isAdesione) "ADESIONE" else "NON_ADESIONE"}")
        Log.d("SeniorSittingForm", "FormId: $formId")
        Log.d("SeniorSittingForm", "UserId: $userId")
        Log.d("SeniorSittingForm", "PatientCode: ${case.patientCod}")
        Log.d("SeniorSittingForm", "Caregiver: ${selectedCaregiver.fullname} (ID: ${selectedCaregiver.id})")

        if (!validateFormAndUpdateState(currentState)) {
            Log.e("SeniorSittingForm", "Form validation failed!")
            return
        }

        Log.d("SeniorSittingForm", "Form validation passed, starting save...")
        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            try {
                val formToSave = currentState.form.copy(lastModified = System.currentTimeMillis())
                
                // Log form sections and questions
                formToSave.sections.forEachIndexed { sectionIndex, section ->
                    Log.d("SeniorSittingForm", "Section $sectionIndex (${section.sectionId}):")
                    section.questions.forEachIndexed { questionIndex, question ->
                        Log.d("SeniorSittingForm", "  Q$questionIndex (ID: ${question.questionId}): score=${question.score}, text='${question.questionText}'")
                    }
                }

                val data = if (isAdesione){
                    Log.d("SeniorSittingForm", "Creating ADESIONE data object...")
                    val adesioneData = SeniorSittingScalePost.SeniorSittingScale(
                        id = formId?.toInt(),
                        cODCase = case.patientCod.toString(),
                        evalDateTime = Date(formToSave.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                        tableARow1 = formToSave.sections[0].questions[0].score ?: 0,
                        tableARow2 = formToSave.sections[0].questions[1].score ?: 0,
                        tableARow3 = formToSave.sections[0].questions[2].score ?: 0,
                        tableARow4 = formToSave.sections[0].questions[3].score ?: 0,
                        tableARow5 = formToSave.sections[0].questions[4].score ?: 0,
                        tableARow6 = formToSave.sections[0].questions[5].score ?: 0,
                        tableARow7 = formToSave.sections[0].questions[6].questionText.ifEmpty { "" },
                        tableARow8 = formToSave.sections[1].questions[0].questionText.ifEmpty { "" },
                        iDUser = userId,
                        modality = true,
                        iDContact = selectedCaregiver.id
                    )
                    Log.d("SeniorSittingForm", "ADESIONE Data: tableARow1=${adesioneData.tableARow1}, tableARow2=${adesioneData.tableARow2}, tableARow3=${adesioneData.tableARow3}, tableARow4=${adesioneData.tableARow4}, tableARow5=${adesioneData.tableARow5}, tableARow6=${adesioneData.tableARow6}")
                    Log.d("SeniorSittingForm", "ADESIONE Text: tableARow7='${adesioneData.tableARow7}', tableARow8='${adesioneData.tableARow8}'")
                    adesioneData
                } else {
                    Log.d("SeniorSittingForm", "Creating NON_ADESIONE data object...")
                    val nonAdesioneData = SeniorSittingScalePost.SeniorSittingScale(
                        id = formId?.toInt(),
                        cODCase = case.patientCod.toString(),
                        evalDateTime = Date(formToSave.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                        iDUser = userId,
                        modality = false,
                        iDContact = selectedCaregiver.id,
                        tableBRow1 = formToSave.sections[0].questions[0].score ?: 0,
                        tableBRow2 = formToSave.sections[0].questions[1].score ?: 0,
                        tableBRow3 = formToSave.sections[0].questions[2].score ?: 0,
                        tableBRow4 = formToSave.sections[0].questions[3].score ?: 0,
                        tableBRow5 = formToSave.sections[0].questions[4].score ?: 0,
                        tableBRow6 = formToSave.sections[0].questions[5].score ?: 0,
                        tableBRow7 = formToSave.sections[0].questions[6].score ?: 0,
                        tableBRow8 = formToSave.sections[0].questions[7].score ?: 0,
                        tableBRow9 = formToSave.sections[0].questions[8].score ?: 0,
                        tableBRow10 = formToSave.sections[1].questions[0].questionText.ifEmpty { "" },
                    )
                    Log.d("SeniorSittingForm", "NON_ADESIONE Data: tableBRow1=${nonAdesioneData.tableBRow1}, tableBRow2=${nonAdesioneData.tableBRow2}, tableBRow3=${nonAdesioneData.tableBRow3}")
                    Log.d("SeniorSittingForm", "NON_ADESIONE Text: tableBRow10='${nonAdesioneData.tableBRow10}'")
                    nonAdesioneData
                }

                Log.d("SeniorSittingForm", "Calling API - isEdit: ${formId != null}")
                val result = if (formId != null){
                    formRepository.editSeniorSittingScale(data)
                } else {
                    formRepository.addSeniorSittingTestScale(data)
                }
                Log.d("SeniorSittingForm", "API call completed successfully: $result")
                _uiState.value = SeniorSittingFormUiState.Saved(formToSave)
            } catch (e: IOException) {
                Log.e("SeniorSittingForm", "IOException during save: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore nel salvataggio del form: ${e.message}")
                )
            } catch (e: Exception) {
                Log.e("SeniorSittingForm", "Exception during save: ${e.message}", e)
                _uiState.value = currentState.copy(
                    isSaving = false,
                    validationErrors = listOf("Errore imprevisto nel salvataggio del form: ${e.message}")
                )
            }
        }
    }
}