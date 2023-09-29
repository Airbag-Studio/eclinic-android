package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.OperatingShift
import java.time.LocalTime


fun OperatingShift.isCurrent(): Boolean{
    val now = LocalTime.now()
    return includeTime(now)
}

fun OperatingShift.includeTime(time: LocalTime): Boolean{
    val start = LocalTime.parse(this.startTime)
    val end = LocalTime.parse(this.stopTime)
    return if (start.isAfter(end)) time.isAfter(start) || time.isBefore(end) else time.isAfter(start) && time.isBefore(end)
}