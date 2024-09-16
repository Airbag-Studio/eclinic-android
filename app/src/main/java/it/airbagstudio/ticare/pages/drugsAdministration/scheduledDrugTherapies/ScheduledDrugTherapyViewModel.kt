package it.airbagstudio.ticare.pages.drugsAdministration.scheduledDrugTherapies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.toDate
import it.airbagstudio.ticare.utils.toDayOfWeek
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduledDrugTherapyViewModel @Inject constructor(
    private val agendaTaskRepository: AgendaTaskRepository,
    private val userDetailRepository: UserDetailRepository,
): ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    private  val _scheduledDrugTherapies = MutableStateFlow<List<ScheduledDrugTherapyItem>>(listOf())
    val scheduledDrugTherapies = _scheduledDrugTherapies
    val shifts = listOf(
    "Mattina",
    "Mezzogiorno",
    "Pomeriggio",
    "Sera",
    "Notte"
    )

    fun getScheduledDrugTherapies(code: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val selectedDay =  userDetailRepository.getSelectedDate() ?: return@launch
            val toDay = LocalDate.parse(selectedDay, DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)).atStartOfDay()
            val todayIndex = toDay.dayOfWeek.value - 1
            val monday = toDay.plusDays(- todayIndex.toLong())
            val sunday = monday.plusDays(6)
            val startDate = monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val endDate = sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val res = agendaTaskRepository.getScheduledDrugTherapies(
                start = startDate,
                end = endDate,
                code = code
            )

            _scheduledDrugTherapies.value = res.results?.sortedBy { it.drug }?.map { item ->
                val shiftsDrugAdministrations = mutableListOf<ScheduledDrugTherapyItem.ScheduledDrugTherapyItem>()
                val from = LocalDate.parse(item.from, DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT)).atStartOfDay()

                shifts.forEachIndexed{ index, _ ->
                    val therapiesForShift = item.shiftsDrugAdministration.filter { it.shiftId == index }
                    val quantities = mutableListOf<String?>()
                    val repetition = item.repetitionLabel.filter { it.isDigit() }.toIntOrNull() ?: 0

                    when (item.repetitionCode) {
                        1 -> {
                            for (i in 0..6) {
                                val sum = therapiesForShift.sumOf { it.quantity.toDouble() }
                                if (sum > 0) {
                                    quantities.add(sum.toString())
                                } else {
                                    quantities.add(null)
                                }
                            }
                        }

                        7, 6 -> {
                            for (i in 0..6) {
                                val therapiesForDay = therapiesForShift.firstOrNull {
                                    val dateTimeString = it.fullText.split(" - ")
                                    val dateTime =
                                        dateTimeString[0].toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)
                                    dateTime?.toDayOfWeek() == i
                                }
                                if (therapiesForDay != null) {
                                    quantities.add(therapiesForDay.quantity)
                                } else {
                                    quantities.add(null)
                                }
                            }
                        }
                        2 -> {
                            for (i in 0..6) {
                                val day = monday.plusDays(i.toLong())
                                if (day.dayOfWeek == from.dayOfWeek){
                                    val sum = therapiesForShift.sumOf { it.quantity.toDouble() }
                                    if (sum > 0) {
                                        quantities.add(sum.toString())
                                    } else {
                                        quantities.add(null)
                                    }
                                }else{
                                    quantities.add(null)
                                }
                            }
                        }
                        3 -> {
                            for (i in 0..6) {
                                val day = monday.plusDays(i.toLong())
                                if (day.dayOfMonth == from.dayOfMonth){
                                    val sum = therapiesForShift.sumOf { it.quantity.toDouble() }
                                    if (sum > 0) {
                                        quantities.add(sum.toString())
                                    } else {
                                        quantities.add(null)
                                    }
                                }else{
                                    quantities.add(null)
                                }
                            }
                        }
                        4 -> {
                            for (i in 0..6) {
                                val day = monday.plusDays(i.toLong())
                                val diff = Duration.between(from,day)
                                if (diff.toDays() % repetition.toLong() == 0L){
                                    val sum = therapiesForShift.sumOf { it.quantity.toDouble() }
                                    if (sum > 0) {
                                        quantities.add(sum.toString())
                                    } else {
                                        quantities.add(null)
                                    }
                                }else{
                                    quantities.add(null)
                                }
                            }
                        }
                        5 -> {
                            for (i in 0..6) {
                                val day = monday.plusDays(i.toLong())
                                if (day.dayOfMonth == from.dayOfMonth && (day.monthValue - from.monthValue) % repetition == 0){
                                    val sum = therapiesForShift.sumOf { it.quantity.toDouble() }
                                    if (sum > 0) {
                                        quantities.add(sum.toString())
                                    } else {
                                        quantities.add(null)
                                    }
                                }else{
                                    quantities.add(null)
                                }
                            }
                        }
                    }
                    shiftsDrugAdministrations.add(ScheduledDrugTherapyItem.ScheduledDrugTherapyItem(
                        shiftId = index,
                        quantities = quantities
                    ))
                }
                return@map ScheduledDrugTherapyItem(
                    from = item.from,
                    to = item.to,
                    drug = item.drug,
                    administeringMode = item.administeringMode,
                    isSpecial = item.isSpecial,
                    shiftsDrugAdministration = shiftsDrugAdministrations
                )
            } ?: listOf()
        }


    }

}