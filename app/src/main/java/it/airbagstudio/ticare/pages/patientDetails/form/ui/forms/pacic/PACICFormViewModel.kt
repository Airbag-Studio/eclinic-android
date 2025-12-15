package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.pacic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.form.PACICTestScale
import ch.ticare.eclinic.library.repository.FormRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.pages.patientDetails.form.domain.data.PACICSQuestions
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PACICForm
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.PatientData
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
class PACICFormViewModel @Inject constructor(
    private val formRepository: FormRepository,
    private val userDetailRepository: UserDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PACICFormUiState>(PACICFormUiState.Editing(form = createNewForm()))
    val uiState: StateFlow<PACICFormUiState> = _uiState.asStateFlow()

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
        fun questionId(questionId: Int) = "section_${questionId}"
    }

    private fun createNewForm(): PACICForm {

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

        return PACICForm(
            patientData = PatientData(
                name = user?.name ?: "",
                surname = user?.surname ?: "",
                birthDate = userBirth
            ),
            questions = PACICSQuestions.getQuestions(),
            compilationTimestamp = System.currentTimeMillis()
        )
    }

    fun initForm(formId: String?) {
        val patient = userDetailRepository.getCurrentCase() ?: return
        val patientCode = patient.patientCod ?: return
        viewModelScope.launch {
            _uiState.value = PACICFormUiState.Loading

            try {

                _uiState.value = PACICFormUiState.Editing(
                    form = createNewForm()
                )
                formRepository.getPacicTestScaleList(patientCode).results?.firstOrNull { it.id == formId?.toInt() }
                    ?.let { form ->
                        onQuestionResponseChanged(1, form.tableRow1)
                        onQuestionResponseChanged(2, form.tableRow2)
                        onQuestionResponseChanged(3, form.tableRow3)
                        onQuestionResponseChanged(4, form.tableRow4)
                        onQuestionResponseChanged(5, form.tableRow5)
                        onQuestionResponseChanged(6, form.tableRow6)
                        onQuestionResponseChanged(7, form.tableRow7)
                        onQuestionResponseChanged(8, form.tableRow8)
                        onQuestionResponseChanged(9, form.tableRow9)
                        onQuestionResponseChanged(10, form.tableRow10)
                        onQuestionResponseChanged(11, form.tableRow11)
                    }
            } catch (e: IOException) {
                _uiState.value =
                    PACICFormUiState.Error("Errore nel caricamento del form IPOS: ${e.message}")
            } catch (e: Exception) {
                _uiState.value =
                    PACICFormUiState.Error("Errore imprevisto nel caricamento del form IPOS: ${e.message}")
            }

        }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState is PACICFormUiState.Error) {

        }
    }

    fun onQuestionResponseChanged(
        questionId: Int,
        newScore: Int?
    ) {
        val currentState = _uiState.value
        if (currentState is PACICFormUiState.Editing) {


            val questionIndex =
                currentState.form.questions.indexOfFirst { it.questionId == questionId }
            if (questionIndex == -1) return

            val updatedQuestions = currentState.form.questions.toMutableList()
            val currentQuestion = updatedQuestions[questionIndex]

            updatedQuestions[questionIndex] = currentQuestion.copy(
                score = newScore,
                questionText = currentQuestion.questionText
            )


            val updatedState = currentState.copy(
                form = currentState.form.copy(questions = updatedQuestions)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }
    }


    private fun validateFormAndUpdateState(editingState: PACICFormUiState.Editing,showError: Boolean = false): Boolean {
        val isValid = true
        val newInvalidFieldKeys = mutableSetOf<String>()
        val validationErrors = mutableListOf<String>()
        _uiState.update {
            (it as PACICFormUiState.Editing).copy(
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
        if (currentState !is PACICFormUiState.Editing) return

        if (!validateFormAndUpdateState(currentState, showError = true)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true) }

            val formToSave = currentState.form.copy(
                lastModified = System.currentTimeMillis()
            )

            try {
                val data = PACICTestScale(
                    id = formId?.toIntOrNull(),
                    cODCase = patientCod,
                    iDUser = userId,
                    evalDateTime = Date(formToSave.compilationTimestamp).format(
                        SERVER_PARAMETER_DATE_TIME_FORMAT
                    ),
                    nameSurnameUser = "",
                    tableRow1 = formToSave.questions[0].score,
                    tableRow2 = formToSave.questions[1].score,
                    tableRow3 = formToSave.questions[2].score,
                    tableRow4 = formToSave.questions[3].score,
                    tableRow5 = formToSave.questions[4].score,
                    tableRow6 = formToSave.questions[5].score,
                    tableRow7 = formToSave.questions[6].score,
                    tableRow8 = formToSave.questions[7].score,
                    tableRow9 = formToSave.questions[9].score,
                )
                if (formId == null) {
                    formRepository.addPacicTestScale(data)
                } else {
                    formRepository.editPacicTestScale(data)
                }
                _uiState.value = PACICFormUiState.Saved(formToSave)
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

    fun onCompilationDateTimeSelected(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is PACICFormUiState.Editing) {
            val updatedState = currentState.copy(
                form = currentState.form.copy(compilationTimestamp = timestamp)
            )
            _uiState.update { updatedState }
            validateFormAndUpdateState(updatedState)
        }
    }

}