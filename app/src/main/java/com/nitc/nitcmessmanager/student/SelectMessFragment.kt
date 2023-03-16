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
import com.nitc.nitcmessmanager.databinding.FragmentSelectMessBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Student

class SelectMessFragment : Fragment() {

    lateinit var selectMessBinding: FragmentSelectMessBinding
    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference_conttractor = db.reference.child("contractors")
    private val reference_student = db.reference.child("students")
    var messName : String = " "
    var contractorUid : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selectMessBinding = FragmentSelectMessBinding.inflate(inflater,container,false)

        receiveMessDetails()

        selectMessBinding.progressBar.visibility = View.INVISIBLE

        selectMessBinding.textInputMessName.isEnabled = false
        selectMessBinding.textInputFoodType.isEnabled = false
        selectMessBinding.textInputCostPerDay.isEnabled = false
        selectMessBinding.textInputAvailability.isEnabled = false

        selectMessBinding.buttonSelectMess.setOnClickListener {
            val aval = selectMessBinding.textInputAvailability.text.toString().toInt()
            if(aval == 0){
                Snackbar.make(selectMessBinding.constraintSelectMessLayout,"This mess is full.",
                    Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            }
            else {
                val studentUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                reference_student.orderByChild("studentId").equalTo(studentUid)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (ds in snapshot.children) {
                                Log.d("debug", ds.child("messEnrolled").value.toString())
                                messName = ds.child("messEnrolled").value.toString()

                                if (messName == "") {
                                    val dialog = AlertDialog.Builder(activity)
                                    dialog.setTitle("Are you sure?")
                                    dialog.setCancelable(false)
                                    dialog.setMessage("Once you enroll, you can't change it unless mess bill is generated")
                                    dialog.setNegativeButton(
                                        "No",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            dialog.cancel()
                                        })
                                    dialog.setPositiveButton(
                                        "Yes",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            selectMessBinding.buttonSelectMess.isClickable = false
                                            selectMessBinding.progressBar.visibility = View.VISIBLE
                                            addMessToStudentDb(studentUid.toString())
                                        })
                                    dialog.create().show()
                                } else {
                                    val dialog = AlertDialog.Builder(activity)
                                    dialog.setTitle("Select Mess")
                                    dialog.setCancelable(false)
                                    dialog.setMessage("You have already enrolled in $messName for this month")
                                    dialog.setNegativeButton(
                                        "OK",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            dialog.cancel()
                                        })
                                    dialog.create().show()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }
        }

        return selectMessBinding.root
    }

    private fun addMessToStudentDb(studentUid: String) {
        val map = mutableMapOf<String,Any>()
        map["messEnrolled"] = selectMessBinding.textInputMessName.text.toString()

        Log.d("student id",studentUid)

        reference_student.child(studentUid).updateChildren(map).addOnCompleteListener { task ->
            if(task.isSuccessful){

                var student : Student = Student()
                reference_student.child(studentUid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        student = snapshot.getValue(Student::class.java)!!
                        Log.d("student",student.studentName+" "+student.messEnrolled)

                        reference_conttractor.orderByChild("contractorId").equalTo(contractorUid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(ds in snapshot.children){
                                        val cont : Contractor = ds.getValue(Contractor::class.java)!!
                                        cont.availability = cont.availability.toString().toInt() - 1
                                        cont.studentEnrolled.add(student)
                                        reference_conttractor.child(cont.contractorId).setValue(cont)
                                        Snackbar.make(selectMessBinding.constraintSelectMessLayout,"You have successfully enrolled in ${student.messEnrolled}",
                                            Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()

                                        updateAvailabilityInCardViewFromDb()
                                    }
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
        }

        selectMessBinding.buttonSelectMess.isClickable = true
        selectMessBinding.progressBar.visibility = View.INVISIBLE
    }

    private fun updateAvailabilityInCardViewFromDb() {
        reference_conttractor.orderByChild("contractorId").equalTo(contractorUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    selectMessBinding.textInputAvailability.setText(ds.child("availability").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun receiveMessDetails() {
        selectMessBinding.textInputMessName.setText(arguments?.getString("messName").toString())
        selectMessBinding.textInputFoodType.setText(arguments?.getString("foodType").toString())
        selectMessBinding.textInputCostPerDay.setText(arguments?.getString("costPerDay").toString())
        selectMessBinding.textInputAvailability.setText(arguments?.getString("availability").toString())
        contractorUid = arguments?.getString("contractorId").toString()
    }

}