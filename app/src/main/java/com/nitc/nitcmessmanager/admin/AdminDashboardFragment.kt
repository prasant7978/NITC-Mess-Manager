package com.nitc.nitcmessmanager.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.authentication.LoginActivity
import com.nitc.nitcmessmanager.databinding.ActivityAdminDashboardBinding
import com.nitc.nitcmessmanager.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    lateinit var adminDashboardBinding: FragmentAdminDashboardBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("admin")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adminDashboardBinding = FragmentAdminDashboardBinding.inflate(inflater, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        retrieveAdminInfo(uid)

        adminDashboardBinding.manageStudent.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val manageStudent = ManageStudentFragment()

            fragmentTransaction.replace(R.id.frameLayout,manageStudent)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        return adminDashboardBinding.root
    }

    private fun retrieveAdminInfo(uid : String){
        ref.orderByChild("adminId").equalTo(uid).addListenerForSingleValueEvent(object  :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    adminDashboardBinding.textViewAdminName.setText(ds.child("adminEmail").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}