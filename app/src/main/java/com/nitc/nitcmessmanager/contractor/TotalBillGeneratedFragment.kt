package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentTotalBillGeneratedBinding

class TotalBillGeneratedFragment : Fragment() {

    lateinit var totalBillGeneratedBinding: FragmentTotalBillGeneratedBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference = db.reference.child("contractors")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        totalBillGeneratedBinding = FragmentTotalBillGeneratedBinding.inflate(inflater,container,false)

        totalBillGeneratedBinding.displayTotalStudentEnrolled.text = arguments?.getInt("totalEnrolledStudent").toString()
        totalBillGeneratedBinding.textViewCostPerStudentInt.text = arguments?.getInt("totalCostPerStudent").toString()
        totalBillGeneratedBinding.textViewTotalDue.text = arguments?.getInt("totalDue").toString()

        return totalBillGeneratedBinding.root
    }



}