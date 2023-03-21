package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentContractorProfileBinding
import com.nitc.nitcmessmanager.model.Contractor

class ContractorProfileFragment : Fragment() {

    lateinit var contractorProfileBinding: FragmentContractorProfileBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference = db.reference.child("contractors")

    private var uid = ""
    lateinit var contractor : Contractor
    private var slotFilled = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contractorProfileBinding = FragmentContractorProfileBinding.inflate(inflater,container,false)

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        retrieveDetailsFromDb(uid)

        contractorProfileBinding.textInputAvailability.isEnabled = false
        contractorProfileBinding.textInputContractorEmail.isEnabled = false
        contractorProfileBinding.textInputContractorPass.isEnabled = false

        contractorProfileBinding.buttonUpdateContractor.setOnClickListener {

            val costPerDay = contractorProfileBinding.textInputCostPerDay.text.toString()
            val capacity = contractorProfileBinding.textInputCapacity.text.toString()

            if(costPerDay.toIntOrNull() == null){
                Snackbar.make(contractorProfileBinding.linearLayout,"Please enter numerical value for cost per day.",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else if(capacity.toIntOrNull() == null){
                Snackbar.make(contractorProfileBinding.linearLayout,"Please enter numerical value for capacity.",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else if(capacity.toInt() <= 0){
                Snackbar.make(contractorProfileBinding.linearLayout,"Capacity can't be zero or negative",Snackbar.LENGTH_SHORT).setAction("close",View.OnClickListener {  }).show()
            }
            else if(costPerDay.toInt() <= 0){
                Snackbar.make(contractorProfileBinding.linearLayout,"Cost per day can't be zero or negative",Snackbar.LENGTH_SHORT).setAction("close",View.OnClickListener {  }).show()
            }
            else {
                updateContractorDetails()
            }
        }

        return contractorProfileBinding.root
    }

    private fun retrieveDetailsFromDb(uid:String) {
        reference.orderByChild("contractorId").equalTo(uid).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    contractorProfileBinding.textInputContractorName.setText(ds.child("contractorName").value.toString())
                    contractorProfileBinding.textInputContractorEmail.setText(ds.child("contractorEmail").value.toString())
                    contractorProfileBinding.textInputContractorPass.setText(ds.child("contractorPassword").value.toString())
                    contractorProfileBinding.textInputMessName.setText(ds.child("messName").value.toString())
                    contractorProfileBinding.textInputCostPerDay.setText(ds.child("costPerDay").value.toString())
                    contractorProfileBinding.textInputFoodType.setText(ds.child("foodType").value.toString())
                    contractorProfileBinding.textInputCapacity.setText(ds.child("capacity").value.toString())
                    contractorProfileBinding.textInputAvailability.setText(ds.child("availability").value.toString())
                    slotFilled = ds.child("capacity").value.toString().toInt() - ds.child("availability").value.toString().toInt()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateContractorDetails() {
        contractorProfileBinding.buttonUpdateContractor.isClickable = false
        contractorProfileBinding.progressBar.visibility = View.VISIBLE

        val map = mutableMapOf<String,Any>()
        map["contractorId"] = uid
        map["contractorEmail"] = contractorProfileBinding.textInputContractorEmail.text.toString()
        map["contractorPassword"] = contractorProfileBinding.textInputContractorPass.text.toString()
        map["userType"] = "Contractor"
        map["contractorName"] = contractorProfileBinding.textInputContractorName.text.toString()
        map["costPerDay"] = contractorProfileBinding.textInputCostPerDay.text.toString().toInt()
        map["messName"] = contractorProfileBinding.textInputMessName.text.toString()
        map["foodType"] = contractorProfileBinding.textInputFoodType.text.toString()
        map["capacity"] = contractorProfileBinding.textInputCapacity.text.toString().toInt()

        val updatedAvailability = contractorProfileBinding.textInputCapacity.text.toString().toInt() - slotFilled

        map["availability"] = updatedAvailability

        reference.child(uid).updateChildren(map).addOnCompleteListener { task ->
            if(task.isSuccessful){
                retrieveDetailsFromDb(uid)
                Snackbar.make(contractorProfileBinding.linearLayout,"Your profile has been updated successfully", Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            }
            contractorProfileBinding.buttonUpdateContractor.isClickable = true
            contractorProfileBinding.progressBar.visibility = View.INVISIBLE
        }

    }

}