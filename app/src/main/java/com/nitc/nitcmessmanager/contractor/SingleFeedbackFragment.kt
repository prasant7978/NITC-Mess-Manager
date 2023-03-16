package com.nitc.nitcmessmanager.contractor

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentSingleFeedbackBinding
import com.nitc.nitcmessmanager.model.Contractor

class SingleFeedbackFragment : Fragment() {

    lateinit var singleFeedbackBinding: FragmentSingleFeedbackBinding
    var uid = ""

    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = db.reference.child("contractors")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singleFeedbackBinding = FragmentSingleFeedbackBinding.inflate(inflater,container,false)

        receiveDetailsFromRecyclerView()

        singleFeedbackBinding.buttonDelete.setOnClickListener {
            showAlertMessage()
        }

        return singleFeedbackBinding.root
    }

    private fun showAlertMessage() {
        val dialog = activity?.let { AlertDialog.Builder(it) }
        dialog?.setCancelable(false)
        dialog?.setTitle("Delete Feedback")
        dialog?.setMessage("Are you sure you want to delete this feedback ?")
        dialog?.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })
        dialog?.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            deleteFeedback()
        })
        dialog?.create()?.show()
    }

    private fun deleteFeedback() {
        val contractorId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.child(contractorId).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var contractor = snapshot.getValue(Contractor::class.java)
                if (contractor?.feedbackReceived != null) {
                    for(fd in contractor.feedbackReceived){
                        if(fd.feedbackId == uid){
                            contractor.feedbackReceived.remove(fd)
                            break
                        }
                    }
                    reference.child(contractorId).setValue(contractor).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Snackbar.make(singleFeedbackBinding.constraintLayout,"The feedback has been deleted",
                                Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        else{
                            Snackbar.make(singleFeedbackBinding.constraintLayout,"The deletion was not successful, Please try again!",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun receiveDetailsFromRecyclerView() {
        singleFeedbackBinding.textViewStdName.setText(arguments?.getString("studentName").toString())
        singleFeedbackBinding.textViewStdFeedback.setText(arguments?.getString("feedbackMessage").toString())
        uid = arguments?.getString("feedbackId").toString()
    }

}