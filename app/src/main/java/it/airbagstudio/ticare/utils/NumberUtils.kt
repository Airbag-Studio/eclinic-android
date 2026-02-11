package it.airbagstudio.ticare.utils

import android.icu.number.NumberFormatter
import java.text.DecimalFormat

fun String.isValidVitalParameterValue() : Boolean{
    val splitted = this.split("/")
    if (splitted.count() == 2){
        return splitted[0].toDoubleOrNull() != null && splitted[1].toDoubleOrNull() != null
    }
    return this.toDoubleOrNull() != null
}

fun Double.format(maximumFractionDigits: Int = 1,minimumFractionDigits: Int = 0): String{
    val formatter = DecimalFormat()
    formatter.maximumFractionDigits = maximumFractionDigits
    formatter.minimumFractionDigits = minimumFractionDigits
    return formatter.format(this)
}