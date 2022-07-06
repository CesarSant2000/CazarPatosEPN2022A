package com.santacruz.cesar.cazarpatossc.storage

import android.app.Activity
import android.os.Environment
import com.santacruz.cesar.cazarpatossc.FileHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileStorageManager (private val activity: Activity): FileHandler {
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    override fun saveInformation(dataToStore:Pair<String,String>){
        if (isExternalStorageWritable()) {
            FileOutputStream(
                File(
                    activity.getExternalFilesDir(null),
                    "myLoginInformation.dat"
                )
            ).bufferedWriter().use { outputStream ->
                outputStream.write(dataToStore.first)
                outputStream.write(System.lineSeparator())
                outputStream.write(dataToStore.second)
            }
        }
    }
    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
    override fun readInformation():Pair<String,String>{
        var email = ""
        var clave = ""
        if (isExternalStorageReadable()) {
            try {
                FileInputStream(
                    File(
                        activity.getExternalFilesDir(null),
                        "myLoginInformation.dat"
                    )
                ).bufferedReader().use {
                    val readData = it.readText()
                    val textArray = readData.split(System.lineSeparator())
                    email = textArray[0]
                    clave = textArray[1]
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return (email to clave)
    }
}