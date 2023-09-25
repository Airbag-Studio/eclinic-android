package it.airbagstudio.ticare.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String) : String{
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(this)
}