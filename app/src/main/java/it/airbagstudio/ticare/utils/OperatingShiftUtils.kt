package it.airbagstudio.ticare.utils

import android.util.Log
import ch.ticare.eclinic.library.entity.OperatingShift
import java.time.LocalTime


fun OperatingShift.isCurrent(): Boolean{
    val start = LocalTime.parse(this.startTime)
    val end = LocalTime.parse(this.stopTime)
    val now = LocalTime.now()
    return if (start.isAfter(end)) now.isAfter(start) || now.isBefore(end) else now.isAfter(start) && now.isBefore(end)
}