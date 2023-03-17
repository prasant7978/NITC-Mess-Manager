package com.nitc.nitcmessmanager.student

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.contractor.TotalBillGeneratedFragment
import com.nitc.nitcmessmanager.databinding.FragmentStudentPaymentBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Student

class StudentPaymentFragment : Fragment() {

    lateinit var studentPaymentBinding: FragmentStudentPaymentBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("students")
    var ref_contractor = db.reference.child("contractors")
    var due : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentPaymentBinding = FragmentStudentPaymentBinding.inflate(inflater,container,false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        retrieveDueAmount(uid)

        studentPaymentBinding.buttonPayBill.setOnClickListener {
            payBill(uid)
        }

        return studentPaymentBinding.root
    }

    private fun payBill(uid : String){
        studentPaymentBinding.buttonPayBill.isClickable = false
        if(due != 0){
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Pay Mess Bill")
            dialog.setCancelable(false)
            dialog.setMessage("A amount of " + due + " will be credited from your bank account")
            dialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            dialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                updateDb(uid)

                Snackbar.make(studentPaymentBinding.constraintPaymentLayout,"Your payment was successful", Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            })
            dialog.create().show()
        }
        else{
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Pay Mess Bill")
            dialog.setCancelable(false)
            dialog.setMessage("You don't have any due to pay")
            dialog.setNegativeButton("OK", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            dialog.create().show()
        }
        studentPaymentBinding.buttonPayBill.isClickable = true
    }

    private fun updateDb(uid : String) {
        var bill = 0
        var messName = ""
        ref.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(std in snapshot.children){
                    val student = std.getValue(Student::class.java)
                    if(student != null){
                        bill = student.messBill
                        messName = student.messEnrolled

                        student.messBill = 0
                        student.messEnrolled = ""
                        student.paymentStatus = "paid"

                        ref.child(student.studentId).setValue(student)
                    }
                }

                Log.d("payment",bill.toString())
                Log.d("payment",messName)

                var contractorUid = ""
                ref_contractor.orderByChild("messName").equalTo(messName).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                            contractorUid = ds.child("contractorId").value.toString()
                            Log.d("payment",contractorUid)
                        }

                        Log.d("payment",contractorUid)

                        ref_contractor.orderByChild("contractorId").equalTo(contractorUid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(ds in snapshot.children){
                                        val contractor : Contractor = ds.getValue(Contractor::class.java)!!

                                        Log.d("payment",contractor.availability.toString())
                                        contractor.availability = contractor.availability + 1
                                        Log.d("payment",contractor.availability.toString())
                                        Log.d("payment",contractor.totalDue.toString())
                                        contractor.totalDue = contractor.totalDue - bill
                                        Log.d("payment",contractor.totalDue.toString())
                                        for(std in contractor.studentEnrolled){
                                            if(std.studentId == uid){
                                                contractor.studentEnrolled.remove(std)
                                                break
                                            }
                                        }

                                        ref_contractor.child(contractor.contractorId).setValue(contractor)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        retrieveDueAmount(uid)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun retrieveDueAmount(uid: String){
        ref.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    Log.d("debug",ds.child("messBill").value.toString())
                    due = ds.child("messBill").value.toString().toInt()
                    Log.d("payment",due.toString())
                    studentPaymentBinding.textViewDues.text = ds.child("messBill").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}