package com.nitc.nitcmessmanager.student

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
import com.nitc.nitcmessmanager.databinding.FragmentStudentPaymentBinding

class StudentPaymentFragment : Fragment() {

    lateinit var studentPaymentBinding: FragmentStudentPaymentBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("students")
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

    fun payBill(uid : String){
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
                ref.child(uid).child("messBill").setValue(0)
                retrieveDueAmount(uid)
                Snackbar.make(studentPaymentBinding.constraintPaymentLayout,"Your payment was successful", Snackbar.LENGTH_INDEFINITE).setAction("Close", View.OnClickListener { }).show()
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

    fun retrieveDueAmount(uid: String){
        ref.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    Log.d("debug",ds.child("messBill").value.toString())
                    due = ds.child("messBill").value.toString().toInt()
                    studentPaymentBinding.textViewDues.text = ds.child("messBill").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}