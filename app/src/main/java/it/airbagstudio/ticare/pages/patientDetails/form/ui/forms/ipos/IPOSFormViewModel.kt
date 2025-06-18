package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.form.IPostTestScale
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q10_MODALITA_COMPILAZIONE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q1_CONCERN_1_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q1_CONCERN_2_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q1_CONCERN_3_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2B_ADDITIONAL_SYMPTOM_1_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2B_ADDITIONAL_SYMPTOM_2_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2B_ADDITIONAL_SYMPTOM_3_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_DEBOLEZZA_MANCANZA_ENERGIA_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_DOLORE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_MANCANZA_DI_FIATO_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_NAUSEA_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_PROBLEMI_CAVO_ORALE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_PROBLEMI_MOBILIZZAZIONE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_SCARSO_APPETITO_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_SONNOLENZA_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_STITICHEZZA_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q2_VOMITO_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q3_ANSIA_MALATTIA_TERAPIE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q4_ANSIA_CARI_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q5_DEPRESSIONE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q6_PACE_SE_STESSO_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q7_CONDIVIDERE_STATI_ANIMO_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q8_INFO_RICEVUTE_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.IPOSQuestions.Q9_GESTIONE_PROBLEMI_PRATICI_ID
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.IPOSTimePeriod
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.domain.repository.OldFormRepository
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
class IPOSFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<IPOSFormUiState>(IPOSFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<IPOSFormUiState> = _uiState.asStateFlow()

    var userId: Int? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect {
                userId = it
            }
        }
    }

    object ValidationKeys {
        const val COMPILATION_TIMESTAMP = "compilation_timestamp"
        fun sectionKey(sectionId: String) = "section_${sectionId}"
    }

    private fun createNewForm(): IPOSForm {

        val user = userDetailRepository.getCurrentCase()
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
        val patient = userDetailRepository.getCurrentCase() ?: return
        val patientCode = patient.patientCod ?: return
        viewModelScope.launch {
            _uiState.value = IPOSFormUiState.Loading

            try {

                _uiState.value = IPOSFormUiState.Editing(
                    form = createNewForm()
                )
                formRepository.getIopsScaleList(patientCode).results?.firstOrNull { it.id == formId?.toInt() }
                    ?.let { form ->
                        onTimePeriodChanged(if(form.modality) IPOSTimePeriod.DAYS_3 else IPOSTimePeriod.DAYS_7)
                        onQuestionResponseChanged("Q1", Q1_CONCERN_1_ID, null, form.tableARow1)
                        onQuestionResponseChanged("Q1", Q1_CONCERN_2_ID, null, form.tableARow2)
                        onQuestionResponseChanged("Q1", Q1_CONCERN_3_ID, null, form.tableARow3)
                        onQuestionResponseChanged("Q2", Q2_DOLORE_ID, form.tableBRow1, null)
                        onQuestionResponseChanged(
                            "Q2",
                            Q2_MANCANZA_DI_FIATO_ID,
                            form.tableBRow2,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q2",
                            Q2_DEBOLEZZA_MANCANZA_ENERGIA_ID,
                            form.tableBRow3,
                            null
                        )
                        onQuestionResponseChanged("Q2", Q2_NAUSEA_ID, form.tableBRow4, null)
                        onQuestionResponseChanged("Q2", Q2_VOMITO_ID, form.tableBRow5, null)
                        onQuestionResponseChanged(
                            "Q2",
                            Q2_SCARSO_APPETITO_ID,
                            form.tableBRow6,
                            null
                        )
                        onQuestionResponseChanged("Q2", Q2_STITICHEZZA_ID, form.tableBRow7, null)
                        onQuestionResponseChanged(
                            "Q2",
                            Q2_PROBLEMI_CAVO_ORALE_ID,
                            form.tableBRow8,
                            null
                        )
                        onQuestionResponseChanged("Q2", Q2_SONNOLENZA_ID, form.tableBRow9, null)
                        onQuestionResponseChanged(
                            "Q2",
                            Q2_PROBLEMI_MOBILIZZAZIONE_ID,
                            form.tableBRow10,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q2b",
                            Q2B_ADDITIONAL_SYMPTOM_1_ID,
                            form.tableBRow11,
                            form.tableBRow11Qst
                        )
                        onQuestionResponseChanged(
                            "Q2b",
                            Q2B_ADDITIONAL_SYMPTOM_2_ID,
                            form.tableBRow12,
                            form.tableBRow12Qst
                        )
                        onQuestionResponseChanged(
                            "Q2b",
                            Q2B_ADDITIONAL_SYMPTOM_3_ID,
                            form.tableBRow13,
                            form.tableBRow13Qst
                        )
                        onQuestionResponseChanged(
                            "Q3_Q9",
                            Q3_ANSIA_MALATTIA_TERAPIE_ID,
                            form.tableCRow1,
                            null
                        )
                        onQuestionResponseChanged("Q3_Q9", Q4_ANSIA_CARI_ID, form.tableCRow2, null)
                        onQuestionResponseChanged("Q3_Q9", Q5_DEPRESSIONE_ID, form.tableCRow3, null)
                        onQuestionResponseChanged(
                            "Q3_Q9",
                            Q6_PACE_SE_STESSO_ID,
                            form.tableDRow1,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q3_Q9",
                            Q7_CONDIVIDERE_STATI_ANIMO_ID,
                            form.tableDRow2,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q3_Q9",
                            Q8_INFO_RICEVUTE_ID,
                            form.tableDRow3,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q3_Q9",
                            Q9_GESTIONE_PROBLEMI_PRATICI_ID,
                            form.tableERow1,
                            null
                        )
                        onQuestionResponseChanged(
                            "Q10",
                            Q10_MODALITA_COMPILAZIONE_ID,
                            form.tableFRow1,
                            null
                        )

                    }
            } catch (e: IOException) {
                _uiState.value =
                    IPOSFormUiState.Error("Errore nel caricamento del form IPOS: ${e.message}")
            } catch (e: Exception) {
                _uiState.value =
                    IPOSFormUiState.Error("Errore imprevisto nel caricamento del form IPOS: ${e.message}")
            }

        }
    }

    fun onTimePeriodChanged(newPeriod: IPOSTimePeriod) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
            // Preserve existing answers when changing time period
            val newSections = IPOSQuestions.getInitialSections(newPeriod)
            val preservedSections = newSections.map { newSection ->
                val existingSection = currentState.form.sections.find { it.sectionId == newSection.sectionId }
                if (existingSection != null) {
                    // Preserve existing answers for this section
                    newSection.copy(
                        questions = newSection.questions.map { newQuestion ->
                            val existingQuestion = existingSection.questions.find { it.questionId == newQuestion.questionId }
                            if (existingQuestion != null) {
                                // Keep existing score and text
                                newQuestion.copy(
                                    score = existingQuestion.score,
                                    questionText = existingQuestion.questionText
                                )
                            } else {
                                newQuestion
                            }
                        }
                    )
                } else {
                    newSection
                }
            }
            
            val updatedForm = currentState.form.copy(
                timePeriod = newPeriod,
                sections = preservedSections
            )
            val updatedState = currentState.copy(form = updatedForm)
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
            val updatedState = currentState.copy(
                form = currentState.form.copy(compilationTimestamp = timestamp)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }
    }

    fun onQuestionResponseChanged(
        sectionId: String,
        questionId: Int,
        newScore: Int?,
        newText: String?
    ) {
        val currentState = _uiState.value
        if (currentState is IPOSFormUiState.Editing) {
            val sectionIndex = currentState.form.sections.indexOfFirst { it.sectionId == sectionId }
            if (sectionIndex == -1) return

            val updatedSections = currentState.form.sections.toMutableList()
            val currentSection = updatedSections[sectionIndex]

            val questionIndex =
                currentSection.questions.indexOfFirst { it.questionId == questionId }
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

    private fun validateFormAndUpdateState(editingState: IPOSFormUiState.Editing,showError: Boolean = false): Boolean {
        var isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()

        // Validate all questions in all sections are answered
        editingState.form.sections.forEach { section ->
            when (section.sectionId) {
                "Q1" -> {
                    // Q1 is now optional - no validation required
                    // Users can leave all concerns blank if they wish
                }

                "Q2" -> {
                    // Q2 requires all symptoms to be rated
                    if (section.questions.any { it.score == null }) {
                        newInvalidFieldKeys.add(ValidationKeys.sectionKey(section.sectionId))
                        isValid = false
                    }
                }

                "Q2b" -> {
                    // Q2b is now optional
                    // If text is provided, score is still optional
                    // Users can leave all additional symptoms blank
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

        if (showError && newInvalidFieldKeys.isNotEmpty()) {
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

    fun saveForm(formId: String?) {
        val currentCase = userDetailRepository.getCurrentCase() ?: return
        val userId = userId ?: return
        val patientCod = currentCase.patientCod ?: return

        val currentState = _uiState.value
        if (currentState !is IPOSFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState, showError = true)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            val formToSave = currentState.form.copy(
                lastModified = System.currentTimeMillis()
            )

            try {
                val data = IPostTestScale(
                    id = formId?.toIntOrNull(),
                    cODCase = patientCod,
                    iDUser = userId,
                    evalDateTime = Date(formToSave.compilationTimestamp).format(
                        SERVER_PARAMETER_DATE_TIME_FORMAT
                    ),
                    modality = if (formToSave.timePeriod == IPOSTimePeriod.DAYS_3) true else false,
                    nameSurnameUser = "",
                    tableARow1 = formToSave.sections[0].questions[0].questionText,
                    tableARow2 = formToSave.sections[0].questions[1].questionText,
                    tableARow3 = formToSave.sections[0].questions[2].questionText,
                    tableBRow1 = formToSave.sections[1].questions[0].score ?: 0,
                    tableBRow2 = formToSave.sections[1].questions[1].score ?: 0,
                    tableBRow3 = formToSave.sections[1].questions[2].score ?: 0,
                    tableBRow4 = formToSave.sections[1].questions[3].score ?: 0,
                    tableBRow5 = formToSave.sections[1].questions[4].score ?: 0,
                    tableBRow6 = formToSave.sections[1].questions[5].score ?: 0,
                    tableBRow7 = formToSave.sections[1].questions[6].score ?: 0,
                    tableBRow8 = formToSave.sections[1].questions[7].score ?: 0,
                    tableBRow9 = formToSave.sections[1].questions[8].score ?: 0,
                    tableBRow10 = formToSave.sections[1].questions[9].score ?: 0,
                    tableBRow11 = formToSave.sections[2].questions[0].score ?: 0,
                    tableBRow11Qst = formToSave.sections[2].questions[0].questionText,
                    tableBRow12 = formToSave.sections[2].questions[1].score ?: 0,
                    tableBRow12Qst = formToSave.sections[2].questions[1].questionText,
                    tableBRow13 = formToSave.sections[2].questions[2].score ?: 0,
                    tableBRow13Qst = formToSave.sections[2].questions[2].questionText,
                    tableCRow1 = formToSave.sections[3].questions[0].score ?: 0,
                    tableCRow2 = formToSave.sections[3].questions[1].score ?: 0,
                    tableCRow3 = formToSave.sections[3].questions[2].score ?: 0,
                    tableDRow1 = formToSave.sections[3].questions[3].score ?: 0,
                    tableDRow2 = formToSave.sections[3].questions[4].score ?: 0,
                    tableDRow3 = formToSave.sections[3].questions[5].score ?: 0,
                    tableERow1 = formToSave.sections[3].questions[6].score ?: 0,
                    tableFRow1 = formToSave.sections[4].questions[0].score ?: 0,
                )
                if (formId == null) {
                    formRepository.addIposTestScale(data)
                } else {
                    formRepository.editIposScale(data)
                }
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