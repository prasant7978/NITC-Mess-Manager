package com.nitc.nitcmessmanager.student

import android.content.Intent
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
import com.nitc.nitcmessmanager.databinding.ActivityStudentDashboardBinding
import com.nitc.nitcmessmanager.databinding.FragmentStudentDashboardBinding

class StudentDashboardFragment : Fragment() {

    lateinit var studentDashboardFragmentBinding: FragmentStudentDashboardBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("students")
    var messName : String = ""
    var studentName : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studentDashboardFragmentBinding = FragmentStudentDashboardBinding.inflate(inflater,container,false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        ref.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    Log.d("debug",ds.child("studentName").value.toString())
                    studentDashboardFragmentBinding.textViewName.text = ds.child("studentName").value.toString()
                    studentName = ds.child("studentName").value.toString()
                    messName = ds.child("messEnrolled").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        studentDashboardFragmentBinding.studentProfile.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val studentProfileFragment = StudentProfileFragment()

            fragmentTransaction.replace(R.id.frameLayout,studentProfileFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        studentDashboardFragmentBinding.studentSelectMess.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val showMessListFragment = ShowMessListFragment()

            fragmentTransaction.replace(R.id.frameLayout,showMessListFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        studentDashboardFragmentBinding.constraintLayoutPayment.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val studentPaymentFragment = StudentPaymentFragment()

            fragmentTransaction.replace(R.id.frameLayout,studentPaymentFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        studentDashboardFragmentBinding.buttonFeedback.setOnClickListener {
            if(messName.isEmpty()){
                Snackbar.make(studentDashboardFragmentBinding.linearLayoutStudentDashboard,"You have not enrolled in any mess yet!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Close", View.OnClickListener { }).show()
            }
            else {
                val bundle = Bundle()
                bundle.putString("studentName",studentName)
                bundle.putString("messName",messName)

                val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
                val studentFeedbackFragment = StudentFeedbackFragment()

                studentFeedbackFragment.arguments = bundle

                fragmentTransaction.replace(R.id.frameLayout,studentFeedbackFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        return studentDashboardFragmentBinding.root
    }

}