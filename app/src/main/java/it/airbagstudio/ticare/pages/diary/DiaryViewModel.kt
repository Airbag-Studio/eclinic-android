package it.airbagstudio.ticare.pages.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.DiaryItem
import ch.ticare.eclinic.library.repository.DiaryRepository
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DiaryUIState(
    val items: Map<String, List<DiaryItem>>,
    val homeCareItems: Map<String, List<DiaryItem>>,
    val patientName: String,
    val isLoading: Boolean,
    val clinicType: ClinicType
)

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userDetailRepository: UserDetailRepository,
    private val diaryRepository: DiaryRepository,
    private val homeCareActivitiesRepository: HomeCareActivitiesRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    private val patientCod: String = savedStateHandle[DestinationsArgs.PATIENT_COD]!!
    private val isLoading = MutableStateFlow<Boolean>(false)
    private val items = MutableStateFlow<List<DiaryItem>>(listOf())
    private val homeCareItems = MutableStateFlow<List<DiaryItem>>(listOf())
    private val clinicType = userRepository.getClinicType()
    val case = userDetailRepository.getCurrentCase()
    val uiState = combine(items, isLoading,homeCareItems,clinicType) { items, _,homeCareItems, clinicType->


        DiaryUIState(
            items = items.groupBy {
                val date = it.date.toDate("dd.MM.yyyy")
                date?.format("EEE dd MMMM") ?: it.date
            },
            homeCareItems = homeCareItems.groupBy {
                val date = it.date.toDate("dd.MM.yyyy")
                date?.format("EEE dd MMMM") ?: it.date
            },
            patientName = case?.getCompleteName() ?: "",
            isLoading = false,
            clinicType = clinicType ?: ClinicType.SPITEX
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiaryUIState(
            items = mapOf(),
            homeCareItems = mapOf(),
            patientName = case?.getCompleteName() ?: "",
            isLoading = true,
            clinicType = ClinicType.SPITEX
        )
    )

    init {
        viewModelScope.launch {
            val maxDiaryDays = diaryRepository.getMaxDiaryDays()
            val now = LocalDate.now()
            val from = now.minusDays(maxDiaryDays.toLong())
            launch {
                items.value = diaryRepository.getDiary(
                    patientCod,
                    from.format(dateFormatter),
                    now.format(dateFormatter)
                ).results ?: listOf()
            }
            launch {
                homeCareItems.value = homeCareActivitiesRepository.getHomeCareActivities(
                    code = patientCod,
                    carePlanId = null,
                    start = from.format(dateFormatter),
                    end = now.format(dateFormatter),
                    sortType = 1,
                    skipQualification = true
                ).results?.map {
                    DiaryItem(
                        caseCode = patientCod,
                        date = it.execDateTime,
                        entityName = "HomeCareServiceTask",
                        time = it.execDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT)
                            ?.format("HH:mm") ?: "",
                        patientLbl = "",
                        title = "HomeCareServiceTask",
                        typeLbl = it.type,
                        userLbl = it.user,
                        isScheduledTask = it.isScheduled,
                        duration = it.duration
                    )
                } ?: listOf()
            }
            isLoading.value = false
        }
    }


}