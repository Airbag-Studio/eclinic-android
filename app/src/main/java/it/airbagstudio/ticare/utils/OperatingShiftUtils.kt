package it.airbagstudio.ticare.utils

import ch.ticare.eclinic.library.entity.OperatingShift
import java.time.LocalTime


fun OperatingShift.isCurrent(): Boolean{
    val start = LocalTime.parse(this.startTime)
    val end = LocalTime.parse(this.stopTime)
    val now = LocalTime.now()
    return now.isAfter(start) && now.isBefore(end)
}