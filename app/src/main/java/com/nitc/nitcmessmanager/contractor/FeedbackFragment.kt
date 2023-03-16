package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.admin.StudentAdapter
import com.nitc.nitcmessmanager.databinding.FragmentFeedbackBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Feedback
import com.nitc.nitcmessmanager.model.Student

class FeedbackFragment : Fragment() {

    lateinit var feedbackFragmentBinding : FragmentFeedbackBinding
    var feedbackList = ArrayList<Feedback>()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("contractors")
    lateinit var feedbackAdapter: FeedbackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feedbackFragmentBinding = FragmentFeedbackBinding.inflate(inflater,container,false)

        retrieveFeedbackListFromDb()

        return feedbackFragmentBinding.root
    }

    private fun retrieveFeedbackListFromDb() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        ref.orderByChild("contractorId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                feedbackList.clear()
                for(fd in snapshot.children){
                    val contractor = fd.getValue(Contractor::class.java)
                    val feedback = contractor?.feedbackReceived
                    if(feedback?.isNotEmpty() == true)
                        feedbackList = feedback
                }
                feedbackList.reverse()
                feedbackFragmentBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
                feedbackAdapter = FeedbackAdapter(feedbackList)
                feedbackFragmentBinding.recyclerView.adapter = feedbackAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}