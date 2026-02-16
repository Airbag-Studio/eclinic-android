package it.airbagstudio.ticare.utils

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols


fun String.isValidVitalParameterValue() : Boolean{
    val splitted = this.split("/")
    if (splitted.count() == 2){
        return splitted[0].toDoubleOrNull() != null && splitted[1].toDoubleOrNull() != null
    }
    return this.toDoubleOrNull() != null
}

fun Double.format(maximumFractionDigits: Int = 1,minimumFractionDigits: Int = 0): String{
    val symbols = DecimalFormatSymbols(java.util.Locale("IT"))
    val formatter = DecimalFormat()
    formatter.maximumFractionDigits = maximumFractionDigits
    formatter.minimumFractionDigits = minimumFractionDigits
    formatter.decimalFormatSymbols = symbols
    return formatter.format(this)
}