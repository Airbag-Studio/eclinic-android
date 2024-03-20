package it.airbagstudio.ticare.ui.components.timeTracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.UserMarking
import ch.ticare.eclinic.library.repository.UserMarkingRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class TimeTrackerViewUIState(
    val isEnabled: Boolean,
    val startTime: String,
    val elapsedTime: String,
    val elapsedTimeForDialog: String,
    val elapsedTimeFromLastActivity: Long
)

@HiltViewModel
class TimeTrackerViewModel @Inject constructor(
    private val userMarkingRepository: UserMarkingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var errorMessage by mutableStateOf<String?>(null)

    private val DATE_FORMAT_PATTERN_FROM_SERVER = "dd.MM.yyyy HH:mm"

    private val _clinicType = userRepository.getClinicType()
    val clinicType: StateFlow<ClinicType?> = _clinicType.stateIn(viewModelScope, SharingStarted.Eagerly,null)

    private var startTime = MutableStateFlow<Date?>(null)
    private val trackingTime = MutableStateFlow<String?>(null)
    private val elapsedTimeFromLastActivity = MutableStateFlow<Long>(0)

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            errorMessage = throwable.localizedMessage
        }

    var uiState = combine(
        startTime,
        trackingTime,
        elapsedTimeFromLastActivity
    ) { startTime, trackingTime, elapsedTimeFromLastActivity ->
        val components = trackingTime?.split(":")
        val hour = components?.firstOrNull()?.toIntOrNull() ?: 0
        val minutes = components?.getOrNull(1)?.toIntOrNull() ?: 0
        val elapsedTimeString = String.format("%02dh %02dm", hour, minutes)
        TimeTrackerViewUIState(
            isEnabled = startTime != null,
            startTime = startTime?.format("dd/MM/yyyy - HH:mm") ?: "--",
            elapsedTime = trackingTime ?: "00:00",
            elapsedTimeForDialog = elapsedTimeString,
            elapsedTimeFromLastActivity = elapsedTimeFromLastActivity
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        TimeTrackerViewUIState(false, "--", "00:00", "", 0)
    )

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            userMarkingRepository.getLastMarkingDatetime().map {
                it?.toDate(DATE_FORMAT_PATTERN_FROM_SERVER)
            }.collect {
                startTime.value = it
                updateElapsedTime()
            }
        }
    }

    fun updateElapsedTime() {
        viewModelScope.launch(coroutineExceptionHandler) {
            launch {
                userMarkingRepository.getLastMarkingDatetime()
                    .map { it?.toDate(DATE_FORMAT_PATTERN_FROM_SERVER) }
                    .collect {
                        startTime.value = it
                    }
            }
            launch {
                userMarkingRepository.getStringTimeFromLastMarking()
                    .collect {
                        trackingTime.value = it ?: "00:00"
                    }
            }

        }

    }

    fun stopTracker() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val res = userMarkingRepository.addMarking(
                UserMarking(
                    isIn = false
                )
            )
            res.error?.desc?.let {
                errorMessage = it
            } ?: run {
                startTime.value = null
            }
            updateElapsedTime()
        }
    }

    fun startTracker(onDone: () -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val startDate = Date()
            val res = userMarkingRepository.addMarking(
                UserMarking(
                    isIn = true
                )
            )
            res.error?.desc?.let {
                errorMessage = it
            } ?: run {
                startTime.value = startDate
                onDone()
            }
        }
    }

    fun updateLastMinutesFromLastActivity() {
        viewModelScope.launch(coroutineExceptionHandler) {
            userMarkingRepository.getMinutesFromLastActivity().collect() {
                elapsedTimeFromLastActivity.value = it ?: 0
            }
        }
    }


}