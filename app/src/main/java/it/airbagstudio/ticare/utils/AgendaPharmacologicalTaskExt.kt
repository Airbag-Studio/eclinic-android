package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Locale

fun AgendaPharmacologicalTask.getExpectedTime(): LocalTime{
    return LocalTime.parse(this.expTime)
}

fun AgendaPharmacologicalTask.getExpectedDate(): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return this.execDate?.let { formatter.parse(it) }
}