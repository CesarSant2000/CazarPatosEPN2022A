package com.santacruz.cesar.cazarpatossc.storage

import android.app.Activity
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.santacruz.cesar.cazarpatossc.FileHandler
import com.santacruz.cesar.cazarpatossc.LOGIN_KEY
import com.santacruz.cesar.cazarpatossc.PASSWORD_KEY

class SharedPreferencesManager (private val activity: Activity): FileHandler {
    override fun saveInformation(dataToStore:Pair<String,String>){
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(LOGIN_KEY , dataToStore.first)
        editor.putString(PASSWORD_KEY, dataToStore.second)
        editor.apply()
    }
    override fun readInformation():Pair<String,String>{
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val email = sharedPref.getString(LOGIN_KEY,"").toString()
        val clave = sharedPref.getString(PASSWORD_KEY,"").toString()
        return (email to clave)
    }
}
