package com.nitc.nitcmessmanager.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nitc.nitcmessmanager.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var forgotBinding: ActivityForgotPasswordBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotBinding.root

        setContentView(view)

        forgotBinding.buttonResetPassword.setOnClickListener {
            val email = forgotBinding.editTextResetEmail.text.toString()
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Snackbar.make(forgotBinding.forgotPasswordLayout,"Password reset mail has been sent successfully, check you mail",Snackbar.LENGTH_LONG)
                        .setAction("close",View.OnClickListener {  }).show()
                    finish()
                }
                else{
                    Toast.makeText(this,task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}