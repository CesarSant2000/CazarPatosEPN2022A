package com.santacruz.cesar.cazarpatossc.storage

import android.app.Activity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.santacruz.cesar.cazarpatossc.FileHandler
import com.santacruz.cesar.cazarpatossc.LOGIN_KEY
import com.santacruz.cesar.cazarpatossc.PASSWORD_KEY

class EncryptedSharedPreferencesManager (private val activity: Activity): FileHandler {
    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPrefEncrypted = EncryptedSharedPreferences.create(
        "custom_shared_prefs_encrypted",
        masterKeyAlias,
        activity,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    override fun saveInformation(dataToStore:Pair<String,String>){
        val editor = sharedPrefEncrypted.edit()
        editor.putString(LOGIN_KEY , dataToStore.first)
        editor.putString(PASSWORD_KEY, dataToStore.second)
        editor.apply()
    }
    override fun readInformation():Pair<String,String>{
        val email = sharedPrefEncrypted.getString(LOGIN_KEY,"").toString()
        val clave = sharedPrefEncrypted.getString(PASSWORD_KEY,"").toString()
        return (email to clave)
    }
}