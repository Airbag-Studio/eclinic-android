package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

fun AgendaPharmacologicalTask.getExpectedTime(): LocalTime?{
    return try {
        LocalTime.parse(this.expTime)
    }catch (e: Throwable){
        null
    }

}

fun AgendaPharmacologicalTask.getExpectedDate(): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return this.execDate?.let { formatter.parse(it) }
}
fun AgendaPharmacologicalTask.validated() : Boolean {
    return !this.colorStatus.equals("rosso",true)
}