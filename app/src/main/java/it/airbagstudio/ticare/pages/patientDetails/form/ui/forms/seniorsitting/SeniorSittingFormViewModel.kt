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

    fun  selectCaregiver(contact: Contact){
        val currentState = _uiState.value
        if (currentState is SeniorSittingFormUiState.Editing) {
            val updatedForm = currentState.form.copy(
                selectedCaregiver = contact
            )
            _uiState.update {
                currentState.copy(form = updatedForm)
            }
            validateFormAndUpdateState(currentState)
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
            validateFormAndUpdateState(currentState)
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
            validateFormAndUpdateState(currentState)
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
            validateFormAndUpdateState(currentState)
        }

    }

    private fun validateFormAndUpdateState(editingState: SeniorSittingFormUiState.Editing): Boolean {
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()
        val selectedCaregiver = editingState.form.selectedCaregiver ?: return false
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

    fun saveForm(formId: String?) {
        val currentState = _uiState.value
        val case = userDetailRepository.getCurrentCase() ?: return
        val userId = userId ?: return
        val selectedCaregiver = (currentState as? SeniorSittingFormUiState.Editing)?.form?.selectedCaregiver ?: return

        val isAdesione = currentState.form.type == SeniorSittingType.ADESIONE

        if (!validateFormAndUpdateState(currentState)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            try {
                val formToSave = currentState.form.copy(lastModified = System.currentTimeMillis())

                val data = if (isAdesione){
                    SeniorSittingScalePost.SeniorSittingScale(
                        id = formId?.toInt(),
                        cODCase = case.patientCod.toString(),
                        evalDateTime = Date(formToSave.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                        tableARow1 = formToSave.sections[0].questions[0].score,
                        tableARow2 = formToSave.sections[0].questions[1].score,
                        tableARow3 = formToSave.sections[0].questions[2].score,
                        tableARow4 = formToSave.sections[0].questions[3].score,
                        tableARow5 = formToSave.sections[0].questions[4].score,
                        tableARow6 = formToSave.sections[0].questions[5].score,
                        tableARow7 = formToSave.sections[0].questions[6].questionText,
                        tableARow8 = formToSave.sections[1].questions[0].questionText,
                        iDUser = userId,
                        modality = true,
                        iDContact = selectedCaregiver.id

                    )
                } else {
                    SeniorSittingScalePost.SeniorSittingScale(
                        id = formId?.toInt(),
                        cODCase = case.patientCod.toString(),
                        evalDateTime = Date(formToSave.compilationTimestamp).format(SERVER_PARAMETER_DATE_TIME_FORMAT),
                        iDUser = userId,
                        modality = false,
                        iDContact = selectedCaregiver.id,
                        tableBRow1 = formToSave.sections[0].questions[0].score,
                        tableBRow2 = formToSave.sections[0].questions[1].score,
                        tableBRow3 = formToSave.sections[0].questions[2].score,
                        tableBRow4 = formToSave.sections[0].questions[3].score,
                        tableBRow5 = formToSave.sections[0].questions[4].score,
                        tableBRow6 = formToSave.sections[0].questions[5].score,
                        tableBRow7 = formToSave.sections[0].questions[6].score,
                        tableBRow8 = formToSave.sections[0].questions[7].score,
                        tableBRow9 = formToSave.sections[0].questions[8].score,
                        tableBRow10 = formToSave.sections[1].questions[0].questionText,

                    )
                }

                if (formId != null){
                    formRepository.editSeniorSittingScale(data)
                } else {
                    formRepository.addSeniorSittingTestScale(data)
                }
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