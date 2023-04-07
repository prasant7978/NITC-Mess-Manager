package com.nitc.nitcmessmanager.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.admin.AdminDashboardActivity
import com.nitc.nitcmessmanager.contractor.ContractorDashboard
import com.nitc.nitcmessmanager.databinding.ActivityLoginBinding
import com.nitc.nitcmessmanager.student.StudentDashboardActivity

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding : ActivityLoginBinding
    private var userType : String = "Student"
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val studentReference = db.reference.child("students")
    private val adminReference = db.reference.child("admin")
    private val contractorReference = db.reference.child("contractors")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root

        setContentView(view)

        loginBinding.progressBarLogin.visibility = View.INVISIBLE

        loginBinding.adminLoginTypeButton.setOnClickListener {
            loginBinding.adminLoginTypeImage.setBackgroundResource(R.drawable.login_type_shape)
            loginBinding.adminLoginTypeButton.setTextColor(Color.WHITE)
            loginBinding.studentLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.studentLoginTypeButton.setTextColor(Color.BLACK)
            loginBinding.contractorLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.contractorLoginTypeButton.setTextColor(Color.BLACK)

            userType = "Admin"

            loginBinding.textViewSignup.visibility = View.INVISIBLE
        }

        loginBinding.studentLoginTypeButton.setOnClickListener {
            loginBinding.adminLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.adminLoginTypeButton.setTextColor(Color.BLACK)
            loginBinding.studentLoginTypeImage.setBackgroundResource(R.drawable.login_type_shape)
            loginBinding.studentLoginTypeButton.setTextColor(Color.WHITE)
            loginBinding.contractorLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.contractorLoginTypeButton.setTextColor(Color.BLACK)

            userType = "Student"

            loginBinding.textViewSignup.visibility = View.VISIBLE
        }

        loginBinding.contractorLoginTypeButton.setOnClickListener {
            loginBinding.adminLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.adminLoginTypeButton.setTextColor(Color.BLACK)
            loginBinding.studentLoginTypeImage.setBackgroundResource(R.drawable.login_type_transparent_bg_shape)
            loginBinding.studentLoginTypeButton.setTextColor(Color.BLACK)
            loginBinding.contractorLoginTypeImage.setBackgroundResource(R.drawable.login_type_shape)
            loginBinding.contractorLoginTypeButton.setTextColor(Color.WHITE)

            userType = "Mess Contractor"

            loginBinding.textViewSignup.visibility = View.INVISIBLE
        }

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
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if(user != null){
            val uid = user.uid

            studentReference.orderByChild("studentId").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
//                            Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_SHORT).show()
                            if(auth.currentUser?.isEmailVerified == true) {
                                val intent =
                                    Intent(this@LoginActivity, StudentDashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            adminReference.orderByChild("adminId").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
//                            Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity,AdminDashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            contractorReference.orderByChild("contractorId").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
//                            Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity,ContractorDashboard::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun signinWithFirebase(email:String, pass:String, userType:String){
        loginBinding.buttonSignin.isClickable = false
        loginBinding.progressBarLogin.visibility = View.VISIBLE
        when (userType) {
            "Student" -> {
                if (!checkConstraints(email)) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter a valid nitc email id",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginBinding.buttonSignin.isClickable = true
                    loginBinding.progressBarLogin.visibility = View.INVISIBLE
                }
                else {
                    Log.d("user", auth.currentUser?.isEmailVerified.toString())
                    Log.d("user", auth.currentUser?.uid.toString())
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (auth.currentUser?.isEmailVerified == true) {
                                Toast.makeText(this, "Email is verified", Toast.LENGTH_SHORT).show()
                                studentReference.orderByChild("studentEmail").equalTo(email)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                auth.signInWithEmailAndPassword(email, pass)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
//                                        Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
                                                            val intent = Intent(
                                                                this@LoginActivity,
                                                                StudentDashboardActivity::class.java
                                                            )
                                                            intent.putExtra("userType", userType)
                                                            startActivity(intent)
                                                            loginBinding.buttonSignin.isClickable =
                                                                true
                                                            loginBinding.progressBarLogin.visibility =
                                                                View.INVISIBLE
                                                            finish()
                                                        }
                                                        else {
                                                            Toast.makeText(
                                                                applicationContext,
                                                                task.exception?.localizedMessage.toString(),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            loginBinding.buttonSignin.isClickable =
                                                                true
                                                            loginBinding.progressBarLogin.visibility =
                                                                View.INVISIBLE
                                                        }
                                                    }
                                            }
                                            else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Incorrect username or password",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loginBinding.buttonSignin.isClickable = true
                                                loginBinding.progressBarLogin.visibility =
                                                    View.INVISIBLE
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })
                            }
                            else {
                                Toast.makeText(
                                    this,
                                    "Please verify your email first",
                                    Toast.LENGTH_SHORT
                                ).show()
                                auth.signOut()
                                loginBinding.buttonSignin.isClickable = true
                                loginBinding.progressBarLogin.visibility = View.INVISIBLE
                            }
                        }
                        else{
                            Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                            loginBinding.buttonSignin.isClickable = true
                            loginBinding.progressBarLogin.visibility = View.INVISIBLE
                        }
                    }
                }
            }

            "Admin" -> {
                adminReference.orderByChild("adminEmail").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
//                                        Toast.makeText(applicationContext, "Welcome Admin", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                                        startActivity(intent)
                                        loginBinding.buttonSignin.isClickable = true
                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE
                                        finish()
                                    } else {
                                        Toast.makeText(applicationContext, task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                                        loginBinding.buttonSignin.isClickable = true
                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE
                                    }
                                }
                            }
                            else{
                                Toast.makeText(applicationContext, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                                loginBinding.buttonSignin.isClickable = true
                                loginBinding.progressBarLogin.visibility = View.INVISIBLE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }

            "Mess Contractor" -> {
                contractorReference.orderByChild("contractorEmail").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
//                                        Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@LoginActivity, ContractorDashboard::class.java)
                                        intent.putExtra("userType",userType)
                                        startActivity(intent)
                                        loginBinding.buttonSignin.isClickable = true
                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE
                                        finish()
                                    } else {
                                        Toast.makeText(applicationContext, task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                                        loginBinding.buttonSignin.isClickable = true
                                        loginBinding.progressBarLogin.visibility = View.INVISIBLE
                                    }
                                }
                            }
                            else{
                                Toast.makeText(applicationContext, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                                loginBinding.buttonSignin.isClickable = true
                                loginBinding.progressBarLogin.visibility = View.INVISIBLE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })


//                loginBinding.buttonSignin.isClickable = true
//                loginBinding.progressBarLogin.visibility = View.INVISIBLE
//                val intent = Intent(this@LoginActivity, ContractorDashboard::class.java)
//                startActivity(intent)
//                finish()
            }
            "" -> {
                loginBinding.buttonSignin.isClickable = true
                loginBinding.progressBarLogin.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "Select a user type", Toast.LENGTH_SHORT).show()
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