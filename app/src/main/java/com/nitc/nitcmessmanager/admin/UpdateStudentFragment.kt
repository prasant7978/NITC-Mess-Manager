package com.nitc.nitcmessmanager.admin

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentUpdateStudentBinding
import com.nitc.nitcmessmanager.model.Student

class UpdateStudentFragment : Fragment() {

    lateinit var updateStudentBinding: FragmentUpdateStudentBinding
    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = db.reference.child("students")

    private var messBill : Int = 0
    private var paymentStatus : String = ""
    private var uid : String = ""

    lateinit var student: Student

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateStudentBinding = FragmentUpdateStudentBinding.inflate(inflater,container,false)

        updateStudentBinding.textInputEmail.isEnabled = false
        updateStudentBinding.textInputPass.isEnabled = false

        receiveStudentDetails()

        updateStudentBinding.buttonUpdateStudent.setOnClickListener {
            updateStudentBinding.buttonUpdateStudent.isClickable = false
            updateStudentBinding.buttonDeleteStudent.isClickable = false
            updateStudentBinding.progressBar.visibility = View.VISIBLE
            updateStudentDetails()
        }

        updateStudentBinding.buttonDeleteStudent.setOnClickListener {
            showAlertMessage()
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
            updateStudentBinding.buttonUpdateStudent.isClickable = true
            updateStudentBinding.buttonDeleteStudent.isClickable = true
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
                    if(ds.child("messEnrolled").value.toString() == "")
                        updateStudentBinding.textInputMessName.setText("Not enrolled yet")
                    else
                        updateStudentBinding.textInputMessName.setText(ds.child("messEnrolled").value.toString())
                    updateStudentBinding.textInputPass.setText(ds.child("studentPassword").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun deleteStudent(){
        reference.child(uid).removeValue().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val auth = FirebaseAuth.getInstance()

                reference.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(ds in snapshot.children){
                            student = ds.getValue(Student::class.java)!!
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                clearAllTextArea()
                Snackbar.make(updateStudentBinding.linearLayout,"The student has been deleted",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            else{
                Snackbar.make(updateStudentBinding.linearLayout,"The deletion was not successful, Please try again!",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
        }
    }

    private fun showAlertMessage(){
        val dialog = activity?.let { AlertDialog.Builder(it) }
        dialog?.setCancelable(false)
        dialog?.setTitle("Delete Student")
        dialog?.setMessage("Are you sure you want to delete this student ?")
        dialog?.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })
        dialog?.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            deleteStudent()
        })
        dialog?.create()?.show()
    }

    fun clearAllTextArea(){
        updateStudentBinding.textInputName.setText("")
        updateStudentBinding.textInputEmail.setText("")
        updateStudentBinding.textInputPass.setText("")
        updateStudentBinding.textInputRoll.setText("")
        updateStudentBinding.textInputMessName.setText("")
    }
}