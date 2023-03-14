package com.nitc.nitcmessmanager.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentStudentFeedbackBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Feedback

class StudentFeedbackFragment : Fragment() {

    lateinit var studentFeedbackBinding: FragmentStudentFeedbackBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference = db.reference.child("contractors")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentFeedbackBinding = FragmentStudentFeedbackBinding.inflate(inflater,container,false)

        studentFeedbackBinding.buttonSend.setOnClickListener {
            studentFeedbackBinding.buttonSend.isClickable = false
            val msg = studentFeedbackBinding.feedbackMessage.text.toString()
            if(msg.isEmpty())
                Toast.makeText(activity,"Please write any feedback to submit", Toast.LENGTH_SHORT).show()
            else
                sendFeedback(msg)
        }

        return studentFeedbackBinding.root
    }

    fun sendFeedback(msg : String){
        val studentName = arguments?.getString("studentName").toString()
        val messName = arguments?.getString("messName").toString()

        reference.orderByChild("messName").equalTo(messName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(ds in snapshot.children){
//                        Log.d("debug",ds.child("contractorName").value.toString())
                        val cont : Contractor = ds.getValue(Contractor::class.java)!!
//                        Log.d("debug",cont.contractorName+" "+cont.messName)
//                        val feedbackSize = cont.feedbackReceived.size
//                        val lastFeedbackId = cont.feedbackReceived[feedbackSize-1].feedbackId
                        val id = reference.push().key.toString()
                        val feedback = Feedback(id,msg,studentName)
                        cont.feedbackReceived.add(feedback)
                        reference.child(cont.contractorId).setValue(cont)
                        Snackbar.make(studentFeedbackBinding.constraintLayoutFeedback,"Thank you for taking the time to provide your feedback.",
                            Snackbar.LENGTH_INDEFINITE).setAction("Close", View.OnClickListener { }).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        studentFeedbackBinding.feedbackMessage.setText("")
        studentFeedbackBinding.buttonSend.isClickable = true
    }

}