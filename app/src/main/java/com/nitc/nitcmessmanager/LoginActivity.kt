package com.nitc.nitcmessmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var loginBinding : ActivityLoginBinding
    lateinit var userType : String
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val studentReference = db.reference.child("students")
    val adminReference = db.reference.child("admin")
    val contractorReference = db.reference.child("mess_contractor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root

        setContentView(view)

        loginBinding.spinner.onItemSelectedListener = this

        loginBinding.progressBarLogin.visibility = View.INVISIBLE

        val arrayAdapter = ArrayAdapter.createFromResource(this,R.array.user_type,android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loginBinding.spinner.adapter=arrayAdapter

        loginBinding.buttonSignin.setOnClickListener {
            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()
            if(userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(applicationContext, "Please enter both email and password to login.", Toast.LENGTH_SHORT).show()
            }
            else {
                signinWithFirebase(userEmail, userPassword, userType)
            }
        }

        loginBinding.textViewSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        val user = auth.currentUser
//        if(user != null){
//            Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_SHORT).show()
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }

    private fun signinWithFirebase(email:String, pass:String, userType:String){
        loginBinding.buttonSignin.isClickable = false
        loginBinding.progressBarLogin.visibility = View.VISIBLE
        when (userType) {
            "Student" -> {
                studentReference.orderByChild("studentEmail").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                studentReference.orderByChild("studentPassword").equalTo(pass)
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()){
                                                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
                                                        loginBinding.buttonSignin.isClickable = true
                                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE

                                                        val intent = Intent(this@LoginActivity, StudentDashboardActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            task.exception?.localizedMessage.toString(),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                            else{
                                                Toast.makeText(applicationContext, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                            else{
                                Toast.makeText(applicationContext, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }
            "Admin" -> {
                adminReference.orderByChild("adminEmail").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                adminReference.orderByChild("adminPassword").equalTo(pass)
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()){
                                                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(applicationContext, "Welcome Admin", Toast.LENGTH_SHORT).show()
                                                        loginBinding.buttonSignin.isClickable = true
                                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE
                                                        val intent = Intent(this@LoginActivity, AdminDashboard::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            task.exception?.localizedMessage.toString(),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                            else{
                                                Toast.makeText(applicationContext, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                            else{
                                Toast.makeText(applicationContext, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }
            "Mess Contractor" -> {

            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent != null){
           userType  = parent.getItemAtPosition(position).toString()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}