package com.nitc.nitcmessmanager.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nitc.nitcmessmanager.databinding.ActivityStudentUpdateProfileBinding
import com.nitc.nitcmessmanager.model.Student

class StudentUpdateProfileActivity : AppCompatActivity() {

    lateinit var updateProfileBinding: ActivityStudentUpdateProfileBinding

    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = db.reference.child("students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateProfileBinding = ActivityStudentUpdateProfileBinding.inflate(layoutInflater)
        val view = updateProfileBinding.root

        setContentView(view)

        supportActionBar?.title = "Profile"

        getAndSet()

    }

    fun getAndSet(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                if(student != null){
                    updateProfileBinding.textViewStudentName.setText("Name : "+student.studentName)
                    updateProfileBinding.textViewStudentRoll.setText("Registration No : "+student.studentRollNo)
                    if(student.messEnrolled.isEmpty())
                        updateProfileBinding.textViewMessEnrolled.setText("Mess : Not enrolled yet")
                    else
                        updateProfileBinding.textViewMessEnrolled.setText("Mess : "+student.messEnrolled)
                    updateProfileBinding.textViewMessBill.setText("Due : "+student.messBill)
                    if(student.paymentStatus == "paid")
                        updateProfileBinding.textViewPaymentStatus.setText("Payment Status : Paid")
                    else
                        updateProfileBinding.textViewPaymentStatus.setText("Payment Status : "+student.paymentStatus)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}