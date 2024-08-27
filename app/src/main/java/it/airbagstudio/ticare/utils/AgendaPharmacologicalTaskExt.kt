package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.AgendaTask
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

fun AgendaTask.getExpectedTime(): LocalTime? {
    return try {
        LocalTime.parse(this.expTime)
    } catch (e: Throwable) {
        null
    }

}

fun AgendaTask.getExecTime(): LocalTime? {
    return try {
        LocalTime.parse(this.execTime)
    } catch (e: Throwable) {
        null
    }

}

fun AgendaTask.getExecDateTime(): Date? {
    if (execDate == null && execTime == null){
        return null
    }
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val value = "$execDate $execTime"
        return formatter.parse(value)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}
fun AgendaTask.validated(): Boolean {
    return !this.colorStatus.equals("rosso", true)
}

fun AgendaTask.isSpecial(): Boolean{
    return this.typeIsSpecial || this.isSchedulerSpecial
}