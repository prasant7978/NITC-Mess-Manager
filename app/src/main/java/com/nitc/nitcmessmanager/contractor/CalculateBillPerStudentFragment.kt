package com.nitc.nitcmessmanager.contractor

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentCalculateBillPerStudentBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Student

class CalculateBillPerStudentFragment : Fragment() {

    lateinit var calculateBillPerStudentBinding: FragmentCalculateBillPerStudentBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference = db.reference.child("contractors")
    val reference_student = db.reference.child("students")
    private var totalEnrolledStudent = 0
    private var messName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calculateBillPerStudentBinding = FragmentCalculateBillPerStudentBinding.inflate(inflater,container,false)

        retrieveCostPerDayFromDb()

        calculateBillPerStudentBinding.buttonGenerateBill.setOnClickListener {
            if(calculateBillPerStudentBinding.textInputLayoutNoOfDaysInt.text.isEmpty()){
                Toast.makeText(activity,"Please, enter number of days to calculate",Toast.LENGTH_SHORT).show()
            }else{
                showAlertMessage()
            }
        }

        return calculateBillPerStudentBinding.root
    }

    private fun showAlertMessage() {
        val dialog = activity?.let { AlertDialog.Builder(it) }
        dialog?.setCancelable(false)
        dialog?.setTitle("Generate Bill")
        dialog?.setMessage("Confirm generate bill")
        dialog?.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })
        dialog?.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            generateBill()
        })
        dialog?.create()?.show()
    }

    private fun generateBill() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val cost = calculateBillPerStudentBinding.displayCostPerDayInt.text.toString().toInt()
        val days = calculateBillPerStudentBinding.textInputLayoutNoOfDaysInt.text.toString().toInt()

        val totalCostPerStudent = cost * days

        val totalDue = totalEnrolledStudent * totalCostPerStudent

//        val map = mutableMapOf<String,Any>()
//        map["totalDue"] = totalDue

        var updatedStudentList = ArrayList<Student>()

        reference_student.orderByChild("messEnrolled").equalTo(messName).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                updatedStudentList.clear()
                for(std in snapshot.children){
                    val student = std.getValue(Student::class.java)
                    if(student != null){
                        student.messBill = totalCostPerStudent
                        student.paymentStatus = "not-paid"
                        reference_student.child(student.studentId).setValue(student)
                        updatedStudentList.add(student)
                    }
                }

                reference.orderByChild("contractorId").equalTo(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(ds in snapshot.children){
                                val cont : Contractor = ds.getValue(Contractor::class.java)!!
                                cont.studentEnrolled.clear()
                                cont.studentEnrolled = updatedStudentList
                                cont.totalDue = totalDue
                                reference.child(cont.contractorId).setValue(cont)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                Snackbar.make(calculateBillPerStudentBinding.constraintLayoutCalculateBillPerStudent,"Bill Generated successfully", Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()

//                reference.child(uid).updateChildren(map).addOnCompleteListener { task ->
//                    if(task.isSuccessful){
                val bundle = Bundle()
                bundle.putInt("totalCostPerStudent",totalCostPerStudent)
                bundle.putInt("totalEnrolledStudent",totalEnrolledStudent)
                bundle.putInt("totalDue",totalDue)

                val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                val totalBillGeneratedFragment = TotalBillGeneratedFragment()

                totalBillGeneratedFragment.arguments = bundle

                fragmentTransaction.replace(R.id.frameLayoutBill,totalBillGeneratedFragment)
                fragmentTransaction.commit()
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveCostPerDayFromDb() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        reference.orderByChild("contractorId").equalTo(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val contractor = ds.getValue(Contractor::class.java)
                    if(contractor != null) {
                        calculateBillPerStudentBinding.displayCostPerDayInt.text = contractor.costPerDay.toString()
                        totalEnrolledStudent = contractor.studentEnrolled.size
                        messName = contractor.messName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}