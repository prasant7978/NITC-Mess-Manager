package com.nitc.nitcmessmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

        signupBinding.buttonSignup.setOnClickListener {
            val name = signupBinding.editTextSignupName.text.toString()
            val roll = signupBinding.editTextSignupRollno.text.toString()
            val email = signupBinding.editTextSignupEmail.text.toString()
            val password = signupBinding.editTextSignupPassword.text.toString()
            signupWithFirebase(email,password,name,roll)
        }
    }

    private fun signupWithFirebase(email:String, password:String, name:String, roll:String){
        signupBinding.progressBarSignup.visibility = View.VISIBLE
        signupBinding.buttonSignup.isClickable = false

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val uid = task.getResult().user?.uid.toString()
                val student = Student(uid,name,email,password,roll,"Student",0,"paid","")

                reference.child(uid).setValue(student)

                Toast.makeText(this,"Your account has been created", Toast.LENGTH_SHORT).show()
                finish()
                signupBinding.progressBarSignup.visibility = View.INVISIBLE
                signupBinding.buttonSignup.isClickable = true
            }
            else{
                Toast.makeText(this,task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}