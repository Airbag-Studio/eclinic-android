package it.airbagstudio.ticare.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

public const val SERVER_DATE_FORMAT = "dd.MM.yyyy"
public const val SERVER_PARAMETER_DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm"
public const val SERVER_PARAMETER_DATE_TIME_FORMAT_ITA = "dd.MM.yyyy HH:mm"
public const val DATE_ONLY_TIME_FORMAT = "HH:mm"



fun Date.format(pattern: String) : String{
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun String.toDate(pattern: String): Date?{
    return try {
        val formatter = SimpleDateFormat(pattern,Locale.getDefault())
        formatter.parse(this)
    }catch (e: Throwable){
        e.printStackTrace()
        null
    }

}