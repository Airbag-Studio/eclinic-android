package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cam

import android.opengl.ETC1.isValid
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.form.CamTestScale
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.CAMQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CAMForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.IPOSFormUiState
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class CAMFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CAMFormUiState>(CAMFormUiState.Loading)
    val uiState: StateFlow<CAMFormUiState> = _uiState.asStateFlow()

    var userId: Int? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserID().collect {
                userId = it
            }
        }
    }

    fun loadForm(formId: Int?) {
        createNewForm(formId = formId)

    }

    private fun createNewForm(formId: Int?) {
        val form = CAMForm(
            id = UUID.randomUUID().toString(),
            // Use getInitialSections to ensure scores are null by default
            sections = CAMQuestions.getInitialSections()
        )
        _uiState.value = CAMFormUiState.Editing(form = form)
        val patient = userDetailRepository.getCurrentCase() ?: return
        val patientCode = patient.patientCod ?: return
        viewModelScope.launch {
            formRepository.getCamTestScaleList(patientCode).results?.firstOrNull { it.iD == formId }?.let { form ->
                onQuestionResponseChanged(1,11,if(form.tableARow1) 1 else 0)
                onQuestionResponseChanged(1,12,if(form.tableARow2) 1 else 0)
                onQuestionResponseChanged(2,2,if(form.tableBRow1) 1 else 0)
                onQuestionResponseChanged(3,3,if(form.tableCRow1) 1 else 0)
                onQuestionResponseChanged(4,4,if(form.tableDRow1) 1 else 0)
            }
        }
    }

    fun onPatientDataChanged(updatedPatientData: PatientData) {
        (_uiState.value as? CAMFormUiState.Editing)?.let { currentState ->
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(patientData = updatedPatientData)
                )
            }
        }
    }

    fun onBirthDateSelected(timestamp: Long?) {
        (_uiState.value as? CAMFormUiState.Editing)?.let { currentState ->
            val newPatientData = currentState.form.patientData.copy(birthDate = timestamp)
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(patientData = newPatientData),
                    isBirthDateValid = timestamp != null // Basic validation: birth date is required
                )
            }
        }
    }

    fun onCompilationDateTimeSelected(timestamp: Long) {
        (_uiState.value as? CAMFormUiState.Editing)?.let { currentState ->
            _uiState.update {
                currentState.copy(
                    form = currentState.form.copy(compilationTimestamp = timestamp)
                )
            }
        }
    }

    fun onQuestionResponseChanged(sectionOrder: Int, questionId: Int, responseScore: Int) {
        (_uiState.value as? CAMFormUiState.Editing)?.let { currentState ->
            val updatedSections = currentState.form.sections.map { section ->
                if (section.order == sectionOrder) {
                    section.copy(questions = section.questions.map { question ->
                        if (question.questionId == questionId) {
                            question.copy(score = responseScore)
                        } else {
                            question
                        }
                    })
                } else {
                    section
                }
            }
            _uiState.update { currentState.copy(form = currentState.form.copy(sections = updatedSections)) }
            validateFormAndUpdateState()
        }
    }
    
    private fun validateFormAndUpdateState(): Boolean {
        val questions = (_uiState.value as? CAMFormUiState.Editing)?.form?.sections?.flatMap { it.questions }
        val allQuestionsAnswered = questions?.all { it.score != null } == true
        _uiState.update {
            (it as CAMFormUiState.Editing).copy(
                isFormValid = allQuestionsAnswered
            )
        }
        return allQuestionsAnswered
    }

    fun saveForm(formId: Int?) {
        val currentCase = userDetailRepository.getCurrentCase() ?: return
        val userId = userId ?: return
        val patientCod = currentCase.patientCod ?: return
        if (!validateFormAndUpdateState()) {
            return // Validation failed
        }

        (_uiState.value as? CAMFormUiState.Editing)?.let { editingState ->
            _uiState.update { editingState.copy(isSaving = true) }
            viewModelScope.launch {
                try {
                    val formToSave = editingState.form.copy(lastModified = System.currentTimeMillis())
                    val toSave = CamTestScale(
                        iD = formId,
                        evalDateTime = Date(formToSave.compilationTimestamp).format(
                            SERVER_PARAMETER_DATE_TIME_FORMAT
                        ),
                        iDUser = userId,
                        cODCase = patientCod,
                        nameSurnameUser = "",
                        tableARow1 = formToSave.sections[0].questions[0].score == 1,
                        tableARow2 = formToSave.sections[0].questions[1].score == 1,
                        tableBRow1 = formToSave.sections[1].questions[0].score == 1,
                        tableCRow1 = formToSave.sections[2].questions[0].score == 1,
                        tableDRow1 = formToSave.sections[3].questions[0].score == 1
                    )
                    if (formId == null) {
                        formRepository.addCamTestScale(toSave)
                    }else{
                        formRepository.editCamTestScale(toSave)
                    }
                    _uiState.value = CAMFormUiState.Saved(formToSave)
                } catch (e: Exception) {
                    _uiState.value = CAMFormUiState.Error("Failed to save form: ${e.message}")
                    // Revert to editing state if save fails, keeping isSaving false
                     _uiState.update { editingState.copy(isSaving = false) }
                }
            }
        }
    }
}
