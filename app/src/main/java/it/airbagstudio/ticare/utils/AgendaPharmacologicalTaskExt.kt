package it.airbagstudio.ticare.utils

import androidx.compose.ui.text.rememberTextMeasurer
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

fun AgendaPharmacologicalTask.getExpectedTime(): LocalTime? {
    return try {
        LocalTime.parse(this.expTime)
    } catch (e: Throwable) {
        null
    }

}

fun AgendaPharmacologicalTask.getExecTime(): LocalTime? {
    return try {
        LocalTime.parse(this.execTime)
    } catch (e: Throwable) {
        null
    }

}

fun AgendaPharmacologicalTask.getExecDateTime(): Date? {
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val value = "$execDate $execTime"
        return formatter.parse(value)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}
fun AgendaPharmacologicalTask.validated(): Boolean {
    return !this.colorStatus.equals("rosso", true)
}