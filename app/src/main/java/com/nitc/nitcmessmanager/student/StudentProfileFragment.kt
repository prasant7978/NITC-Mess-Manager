package com.nitc.nitcmessmanager.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentStudentProfileBinding
import com.nitc.nitcmessmanager.model.Student

class StudentProfileFragment : Fragment() {

    lateinit var studentProfileBinding: FragmentStudentProfileBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = db.reference.child("students")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentProfileBinding = FragmentStudentProfileBinding.inflate(inflater,container,false)

        getAndSet()

        return studentProfileBinding.root
    }

    fun getAndSet(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                if(student != null){
                    studentProfileBinding.textViewStudentName.setText("Name : "+student.studentName)
                    studentProfileBinding.textViewStudentRoll.setText("Registration No : "+student.studentRollNo)
                    if(student.messEnrolled.isEmpty())
                        studentProfileBinding.textViewMessEnrolled.setText("Mess : Not enrolled yet")
                    else
                        studentProfileBinding.textViewMessEnrolled.setText("Mess : "+student.messEnrolled)
                    studentProfileBinding.textViewMessBill.setText("Due : "+student.messBill)
                    if(student.paymentStatus == "paid")
                        studentProfileBinding.textViewPaymentStatus.setText("Payment Status : Paid")
                    else
                        studentProfileBinding.textViewPaymentStatus.setText("Payment Status : "+student.paymentStatus)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}