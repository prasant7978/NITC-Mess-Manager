package com.nitc.nitcmessmanager.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentUpdateStudentBinding

class UpdateStudentFragment : Fragment() {

    lateinit var updateStudentBinding: FragmentUpdateStudentBinding
    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = db.reference.child("students")

    private var messBill : Int = 0
    private var paymentStatus : String = ""
    private var uid : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateStudentBinding = FragmentUpdateStudentBinding.inflate(inflater,container,false)

        receiveStudentDetails()

        updateStudentBinding.buttonUpdateStudent.setOnClickListener {
            updateStudentBinding.buttonUpdateStudent.isCheckable = false
            updateStudentBinding.progressBar.visibility = View.VISIBLE
            updateStudentDetails()
        }

        return updateStudentBinding.root
    }

    private fun receiveStudentDetails(){
        updateStudentBinding.textInputName.setText(arguments?.getString("studentName").toString())
        updateStudentBinding.textInputEmail.setText(arguments?.getString("studentEmail").toString())
        updateStudentBinding.textInputRoll.setText(arguments?.getString("studentRoll").toString())
        updateStudentBinding.textInputMessName.setText(arguments?.getString("studentMess").toString())
        updateStudentBinding.textInputPass.setText(arguments?.getString("studentPass").toString())
        messBill = arguments?.getString("studentBill").toString().toInt()
        paymentStatus = arguments?.getString("studentPaymentStatus").toString()
        uid = arguments?.getString("studentId").toString()
    }

    private fun updateStudentDetails(){
        val map = mutableMapOf<String,Any>()
        map["studentId"] = uid
        map["studentName"] = updateStudentBinding.textInputName.text.toString()
        map["studentEmail"] = updateStudentBinding.textInputEmail.text.toString()
        map["studentPassword"] = updateStudentBinding.textInputPass.text.toString()
        map["studentRollNo"] = updateStudentBinding.textInputRoll.text.toString()
        map["userType"] = "Student"
        map["messBill"] = messBill
        map["paymentStatus"] = paymentStatus
        map["messEnrolled"] = updateStudentBinding.textInputMessName.text.toString()

        reference.child(uid).updateChildren(map).addOnCompleteListener { task ->
            if(task.isSuccessful){
                retrieveDetailsFromDb()
                Snackbar.make(updateStudentBinding.linearLayout,"The user has been updated successfully", Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            }
            updateStudentBinding.buttonUpdateStudent.isCheckable = true
            updateStudentBinding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun retrieveDetailsFromDb(){
        reference.orderByChild("studentId").equalTo(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    updateStudentBinding.textInputName.setText(ds.child("studentName").value.toString())
                    updateStudentBinding.textInputEmail.setText(ds.child("studentEmail").value.toString())
                    updateStudentBinding.textInputRoll.setText(ds.child("studentRollNo").value.toString())
                    updateStudentBinding.textInputMessName.setText(ds.child("messEnrolled").value.toString())
                    updateStudentBinding.textInputPass.setText(ds.child("studentPassword").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}