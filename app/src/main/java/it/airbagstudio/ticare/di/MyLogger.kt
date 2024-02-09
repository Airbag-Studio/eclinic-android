package it.airbagstudio.ticare.di

import android.util.Log
import ch.ticare.eclinic.library.ECLogger

class MyLogger: ECLogger {

    override fun log(tag: String, value: String) {
        Log.w(tag,value)
    }
}