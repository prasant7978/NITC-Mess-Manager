package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.admin.ManageStudentFragment
import com.nitc.nitcmessmanager.databinding.FragmentContractorDashboardBinding
import com.nitc.nitcmessmanager.model.Contractor

class ContractorDashboardFragment : Fragment() {

    lateinit var contractorDashboardBinding: FragmentContractorDashboardBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("contractors")
    var noOfEnrolledStudent = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contractorDashboardBinding = FragmentContractorDashboardBinding.inflate(inflater,container,false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val userType = arguments?.getString("userType")

        retrieveContractorInfo(uid)

        contractorDashboardBinding.profile.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val contractorProfile = ContractorProfileFragment()

            fragmentTransaction.replace(R.id.frameLayout,contractorProfile)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        contractorDashboardBinding.messMenu.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val manageMessMenuFragment = ManageMessMenuFragment()

            val bundle = Bundle()
            bundle.putString("usertype",userType)

            manageMessMenuFragment.arguments = bundle

            fragmentTransaction.replace(R.id.frameLayout,manageMessMenuFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        contractorDashboardBinding.generateBill.setOnClickListener {
            if(noOfEnrolledStudent != 0) {
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                val generateBillFragment = GenerateBillFragment()

                fragmentTransaction.replace(R.id.frameLayout, generateBillFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }else{
                Snackbar.make(contractorDashboardBinding.contractorDashboradLayout,"No student has been enrolled yet.",
                    Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            }
        }

        contractorDashboardBinding.checkFeedback.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val feedbackFragment = FeedbackFragment()

            fragmentTransaction.replace(R.id.frameLayout,feedbackFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        contractorDashboardBinding.enrolledStudent.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val studentEnrolled = EnrolledStudentListFragment()

            fragmentTransaction.replace(R.id.frameLayout,studentEnrolled)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return contractorDashboardBinding.root
    }

    private fun retrieveContractorInfo(uid : String){
        ref.orderByChild("contractorId").equalTo(uid).addListenerForSingleValueEvent(object  :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val contractor = ds.getValue(Contractor::class.java)
                    if(contractor != null){
                        noOfEnrolledStudent = contractor.studentEnrolled.size
                        contractorDashboardBinding.textViewContractorName.setText(contractor.contractorName.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}