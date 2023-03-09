package com.nitc.nitcmessmanager.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.databinding.ActivityStudentFeedbackBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Feedback

class StudentFeedback : AppCompatActivity() {

    lateinit var feedbackBinding: ActivityStudentFeedbackBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference = db.reference.child("contractors")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedbackBinding = ActivityStudentFeedbackBinding.inflate(layoutInflater)
        val view = feedbackBinding.root

        setContentView(view)

        supportActionBar?.title = "Feedback"

        feedbackBinding.buttonSend.setOnClickListener {
            feedbackBinding.buttonSend.isClickable = false
            val msg = feedbackBinding.feedbackMessage.text.toString()
            if(msg.isEmpty())
                Toast.makeText(this,"Please write any feedback to submit",Toast.LENGTH_SHORT).show()
            else
                sendFeedback(msg)
        }
    }

    fun sendFeedback(msg : String){
        val studentName = intent.getStringExtra("studentName").toString()
        val messName = intent.getStringExtra("messName")

        var contractor : Contractor
        reference.orderByChild("messName").equalTo(messName)
            .addListenerForSingleValueEvent(object : ValueEventListener{
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
                        reference.child(cont.contractorId.toString()).setValue(cont)
                        Snackbar.make(feedbackBinding.constraintLayoutFeedback,"Thank you for taking the time to provide your feedback.",
                            Snackbar.LENGTH_INDEFINITE).setAction("Close", View.OnClickListener { }).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        feedbackBinding.feedbackMessage.setText("")
        feedbackBinding.buttonSend.isClickable = true
    }
}