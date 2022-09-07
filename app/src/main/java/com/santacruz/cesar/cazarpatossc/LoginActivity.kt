package com.santacruz.cesar.cazarpatossc

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.santacruz.cesar.cazarpatossc.interfaces.FileHandler
import com.santacruz.cesar.cazarpatossc.storage.FileStorageManager

class LoginActivity : AppCompatActivity() {
    private lateinit var fileHandler: FileHandler
    private lateinit var editTextEmail:EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonNewUser:Button
    private lateinit var checkBoxRememberMe: CheckBox
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+"

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
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe)
        storage = Firebase.storage
        // Initialize Firebase Auth
        auth = Firebase.auth

        readPreferencesData()
        //Click events
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Required data and templates validations
            if(!validateRequiredData(email, clave))
                return@setOnClickListener
            //Saves preferences data.
            savePreferencesData()
            //Login with Firebase
            authenticateUser(email, clave)
            //If pass validation of required data, go to main screen
            //val intention = Intent(this, MainActivity::class.java)
            //intention.putExtra(EXTRA_LOGIN, email)
            //startActivity(intention)
        }
        buttonNewUser.setOnClickListener{
            val intention = Intent(this, SignUpActivity::class.java)
            startActivity(intention)
        }
        mediaPlayer= MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    private fun authenticateUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //If pass validation of required data, go to main screen
                    val intention = Intent(this, MainActivity::class.java)
                    intention.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intention)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun readPreferencesData(){
        val readList = fileHandler.readInformation()
        if (readList.first != ""){
            checkBoxRememberMe.isChecked = true
        }
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

    private fun validateRequiredData(email: String, clave: String):Boolean{
        //get string from resources
        if (email.isEmpty()) {
            editTextEmail.error = getString(R.string.email_requirement_string)
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.error = getString(R.string.password_requirement_string)
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 8) {
            editTextPassword.error = getString(R.string.password_error_lenght_string)
            editTextPassword.requestFocus()
            return false
        }
        if(!email.matches(emailPattern.toRegex())){
            editTextEmail.error = getString(R.string.invalid_email_string)
            editTextEmail.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
