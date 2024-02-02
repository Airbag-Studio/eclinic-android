package it.airbagstudio.ticare.di

import android.content.Context
import android.net.Uri
import android.os.Environment
import ch.ticare.eclinic.library.repository.LocalStorageApi
import io.ktor.http.encodeURLQueryComponent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LocalStorageImpl(private val context: Context): LocalStorageApi {
    override fun clearStorage() {
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        dir.deleteRecursively()
    }

    override fun saveFile(fileName: String, fileContent: ByteArray) {
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        val photo = File(dir, Uri.encode(fileName));
        if (photo.exists()) {
            photo.delete();
        }
        try {
            val fos = FileOutputStream(photo.path);
            fos.write(fileContent);
            fos.close();
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }
}