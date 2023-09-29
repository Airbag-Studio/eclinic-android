package it.airbagstudio.ticare.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


fun LocalTime.printTime(): String{
    val localizedTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return this.format(localizedTimeFormatter)
}