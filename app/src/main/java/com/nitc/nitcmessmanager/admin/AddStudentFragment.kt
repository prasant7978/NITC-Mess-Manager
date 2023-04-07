// Currently no use as for adding a student the admin would be redirect to the student signup page

package com.nitc.nitcmessmanager.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.authentication.SignUpActivity
import com.nitc.nitcmessmanager.databinding.FragmentAddStudentBinding
import com.nitc.nitcmessmanager.model.Student

class AddStudentFragment : Fragment() {

    private lateinit var addStudentBinding: FragmentAddStudentBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference  = db.reference.child("students")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addStudentBinding = FragmentAddStudentBinding.inflate(inflater,container,false)

        addStudentBinding.buttonAddStudent.setOnClickListener {
            val name = addStudentBinding.textInputName.text.toString()
            val email = addStudentBinding.textInputEmail.text.toString()
            val pass = addStudentBinding.textInputPass.text.toString()
            val roll = addStudentBinding.textInputRoll.text.toString().uppercase()
            val mess = addStudentBinding.textInputMessName.text.toString().uppercase()

            if(name.isEmpty() || email.isEmpty() ||pass.isEmpty() ||roll.isEmpty()){
                Toast.makeText(activity,"Please provide complete information",Toast.LENGTH_SHORT).show()
            }
            else {
                if(!checkConstraints(email)){
                    Toast.makeText(context,"Enter a valid nitc email id",Toast.LENGTH_SHORT).show()
                }
                else {
                    addStudentBinding.buttonAddStudent.isCheckable = false
                    addStudentBinding.progressBar.visibility = View.VISIBLE
                    addStudentToDb(name, email, pass, roll, mess)
                }
            }
        }

        addStudentBinding.buttonClearAll.setOnClickListener {
            clearAllTextArea()
        }

        return addStudentBinding.root
    }

    private fun addStudentToDb(name: String, email: String, pass: String, roll: String, mess: String){
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val uid = task.result.user?.uid.toString()
                val student = Student(uid,name,email,pass,roll,"Student",0,"paid",mess)

                reference.child(uid).setValue(student)

                Snackbar.make(addStudentBinding.linearLayout,"Student account created",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
                clearAllTextArea()
            }
            else{
                Toast.makeText(activity,task.exception?.localizedMessage,Toast.LENGTH_LONG).show()
            }
            addStudentBinding.buttonAddStudent.isCheckable = true
            addStudentBinding.progressBar.visibility = View.INVISIBLE
        }
    }

    fun clearAllTextArea(){
        addStudentBinding.textInputName.setText("")
        addStudentBinding.textInputEmail.setText("")
        addStudentBinding.textInputPass.setText("")
        addStudentBinding.textInputRoll.setText("")
        addStudentBinding.textInputMessName.setText("")
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