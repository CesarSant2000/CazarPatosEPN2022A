package com.santacruz.cesar.cazarpatossc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    private lateinit var editTextEmailSignUp: EditText
    private lateinit var editTextPasswordSignUp: EditText
    private lateinit var buttonCreateNewUser: Button
    private lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        editTextEmailSignUp = findViewById(R.id.editTextEmailSignUp)
        editTextPasswordSignUp = findViewById(R.id.editTextPasswordSignUp)
        buttonCreateNewUser = findViewById(R.id.buttonCreateNewUser)
        auth = FirebaseAuth.getInstance()

        buttonCreateNewUser.setOnClickListener {
            val email = editTextEmailSignUp.text.toString()
            val clave = editTextPasswordSignUp.text.toString()
            //Required data and templates validations
            if(!validateRequiredData(email, clave))
                return@setOnClickListener
            //Sign up with email and password
            signUpNewUser(email, clave)
        }
    }

    private fun signUpNewUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(EXTRA_LOGIN, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "New user saved.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(EXTRA_LOGIN, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun validateRequiredData(email: String, clave: String):Boolean{
        //get string from resources
        if (email.isEmpty()) {
            editTextEmailSignUp.error = getString(R.string.email_requirement_string)
            editTextEmailSignUp.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPasswordSignUp.error = getString(R.string.password_requirement_string)
            editTextPasswordSignUp.requestFocus()
            return false
        }
        if (clave.length < 8) {
            editTextPasswordSignUp.error = getString(R.string.password_error_lenght_string)
            editTextPasswordSignUp.requestFocus()
            return false
        }
        if(!email.matches(emailPattern.toRegex())){
            editTextEmailSignUp.error = getString(R.string.invalid_email_string)
            editTextEmailSignUp.requestFocus()
            return false
        }
        return true
    }

    private fun updateUI(user: FirebaseUser?){
        if(user != null){
            Toast.makeText(baseContext, "New user saved.",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}