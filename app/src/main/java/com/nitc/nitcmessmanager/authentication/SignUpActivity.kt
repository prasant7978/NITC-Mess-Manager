package com.nitc.nitcmessmanager.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nitc.nitcmessmanager.databinding.ActivitySignUpBinding
import com.nitc.nitcmessmanager.model.Student

class SignUpActivity : AppCompatActivity() {
    lateinit var signupBinding: ActivitySignUpBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference  = db.reference.child("students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signupBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signupBinding.root

        setContentView(view)

        supportActionBar?.title = "Sign Up"

        signupBinding.progressBarSignup.visibility = View.INVISIBLE

        signupBinding.buttonSignup.setOnClickListener {
            val name = signupBinding.editTextSignupName.text.toString()
            val roll = signupBinding.editTextSignupRollno.text.toString().uppercase()
            val email = signupBinding.editTextSignupEmail.text.toString()
            val password = signupBinding.editTextSignupPassword.text.toString()

            if(name.isEmpty() || email.isEmpty() ||password.isEmpty() ||roll.isEmpty()){
                Toast.makeText(this,"Please provide complete information",Toast.LENGTH_SHORT).show()
            }
            else {
                if(!checkConstraints(email)){
                    Toast.makeText(this@SignUpActivity,"Enter a valid nitc email id",Toast.LENGTH_SHORT).show()
                }
                else {
                    signupWithFirebase(email, password, name, roll)
                }
            }
        }
    }

    private fun signupWithFirebase(email:String, password:String, name:String, roll:String){
        signupBinding.buttonSignup.isClickable = false
        signupBinding.progressBarSignup.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = task.getResult().user?.uid.toString()
                val student = Student(uid, name, email, password, roll, "Student", 0, "paid", "")

                reference.child(uid).setValue(student)

                auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                    Log.d("signup", "mail sent")

                    Toast.makeText(this,"A verification link has been sent to the mail id",Toast.LENGTH_LONG).show()

                    finish()
                }

                signupBinding.buttonSignup.isClickable = true
                signupBinding.progressBarSignup.visibility = View.INVISIBLE
            }
        }
    }

    private fun checkConstraints(email: String): Boolean {
        if(email.contains('_')) {
            val roll = email.substring(email.indexOf("_") + 1, email.length)
            if (roll[0] == 'm' || roll[0] == 'b' || roll[0] == 'p') {
                if(roll.contains('@')) {
                    val domain = roll.substring(roll.indexOf("@") + 1, roll.length)
                    return domain == "nitc.ac.in"
                }
                else{
                    return false
                }
            } else {
                return false
            }
        }
        else{
            return false
        }
    }
}