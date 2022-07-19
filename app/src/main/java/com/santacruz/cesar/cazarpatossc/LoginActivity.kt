package com.santacruz.cesar.cazarpatossc

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.santacruz.cesar.cazarpatossc.storage.EncryptedSharedPreferencesManager
import com.santacruz.cesar.cazarpatossc.storage.FileStorageManager
import com.santacruz.cesar.cazarpatossc.storage.SharedPreferencesManager

class LoginActivity : AppCompatActivity() {
    private lateinit var fileHandler: FileHandler
    private lateinit var editTextEmail:EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonNewUser:Button
    private lateinit var checkBoxRememberMe: CheckBox
    private lateinit var mediaPlayer:MediaPlayer
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Variables initialization
        //fileHandler = SharedPreferencesManager(this)
        //fileHandler = EncryptedSharedPreferencesManager(this)
        fileHandler = FileStorageManager(this)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRememberMe = findViewById(R.id.checkBoxRecordarme)

        readPreferencesData()
        //Click events
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Check valid email
            if(email.isEmpty() || !email.matches(emailPattern.toRegex())){
                editTextEmail.error = getString(R.string.invalid_email_string)
                editTextEmail.requestFocus()
                return@setOnClickListener
            }
            //Check valid password
            if(clave.isEmpty() || clave.length < 8){
                editTextPassword.error = "Ingrese una contraseña valida"
                editTextPassword.requestFocus()
                return@setOnClickListener
            }
            //Required data and templates validations
            if(!validateRequiredData())
                return@setOnClickListener
            //Saves preferences data.
            savePreferencesData()
            //If pass validation of required data, go to main screen
            val intention = Intent(this, MainActivity::class.java)
            intention.putExtra(EXTRA_LOGIN, email)
            startActivity(intention)
        }
        buttonNewUser.setOnClickListener{

        }
        mediaPlayer= MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    private fun readPreferencesData(){
        val readList = fileHandler.readInformation()
        checkBoxRememberMe.isChecked = true
        editTextEmail.setText ( readList.first )
        editTextPassword.setText ( readList.second )
    }

    private fun savePreferencesData(){
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val toSaveList:Pair<String,String> = if(checkBoxRememberMe.isChecked){
            email to clave
        } else{
            "" to ""
        }
        fileHandler.saveInformation(toSaveList)
    }

    private fun validateRequiredData():Boolean{
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.error = "The email is required"
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.error = "The password is required"
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 3) {
            editTextPassword.error = "The password must be at least 3 characters"
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
