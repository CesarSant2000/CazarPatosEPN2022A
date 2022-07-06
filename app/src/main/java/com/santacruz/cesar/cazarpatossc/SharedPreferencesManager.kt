package com.santacruz.cesar.cazarpatossc

import android.app.Activity
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SharedPreferencesManager (private val activity: Activity): FileHandler{
    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    override fun saveInformation(dataToStore:Pair<String,String>){
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(LOGIN_KEY , dataToStore.first)
        editor.putString(PASSWORD_KEY, dataToStore.second)
        editor.apply()
    }
    override fun saveInformationEncrypted(dataToStore:Pair<String,String>){
        val sharedPrefEncrypted = EncryptedSharedPreferences.create(
            "custom_shared_prefs_encrypted",
            masterKeyAlias,
            activity,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPrefEncrypted.edit()
        editor.putString(LOGIN_KEY , dataToStore.first)
        editor.putString(PASSWORD_KEY, dataToStore.second)
        editor.apply()
    }
    override fun saveInformationFile(dataToStore:Pair<String,String>){
        val sharedPrefEncrypted = EncryptedSharedPreferences.create(
            "custom_shared_prefs_encrypted",
            masterKeyAlias,
            activity,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPrefEncrypted.edit()
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
    override fun readInformationEncrypted():Pair<String,String>{
        val sharedPrefEncrypted = EncryptedSharedPreferences.create(
            "custom_shared_prefs_encrypted",
            masterKeyAlias,
            activity,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val email = sharedPrefEncrypted.getString(LOGIN_KEY,"").toString()
        val clave = sharedPrefEncrypted.getString(PASSWORD_KEY,"").toString()
        return (email to clave)
    }
    override fun readInformationFile():Pair<String,String>{
        val sharedPrefEncrypted = EncryptedSharedPreferences.create(
            "custom_shared_prefs_encrypted",
            masterKeyAlias,
            activity,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val email = sharedPrefEncrypted.getString(LOGIN_KEY,"").toString()
        val clave = sharedPrefEncrypted.getString(PASSWORD_KEY,"").toString()
        return (email to clave)
    }
}
