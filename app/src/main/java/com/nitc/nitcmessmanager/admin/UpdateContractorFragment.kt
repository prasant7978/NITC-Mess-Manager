package com.nitc.nitcmessmanager.admin

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentManageContractorBinding
import com.nitc.nitcmessmanager.databinding.FragmentUpdateContractorBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Feedback
import com.nitc.nitcmessmanager.model.Student

class UpdateContractorFragment : Fragment() {
//    private val adminViewModel = ViewModelProvider(requireActivity())[AdminViewModel::class.java]
    lateinit var updateContractorBinding: FragmentUpdateContractorBinding
    private val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference = db.reference.child("contractors")

    private var uid = ""
    lateinit var contractor : Contractor
    private var slotFilled = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateContractorBinding = FragmentUpdateContractorBinding.inflate(inflater,container,false)

        updateContractorBinding.textInputContractorEmail.isEnabled = false
        updateContractorBinding.textInputContractorPass.isEnabled = false

        receiveContractorDetails()

        updateContractorBinding.textInputAvailability.isEnabled = false

        updateContractorBinding.buttonUpdateContractor.setOnClickListener {

            val costPerDay = updateContractorBinding.textInputCostPerDay.text.toString()
            val capacity = updateContractorBinding.textInputCapacity.text.toString()

            if(costPerDay.toIntOrNull() == null){
                Snackbar.make(updateContractorBinding.linearLayout,"Please enter numerical value for cost per day.",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else if(capacity.toIntOrNull() == null){
                Snackbar.make(updateContractorBinding.linearLayout,"Please enter numerical value for capacity.",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else if(costPerDay.toInt() <= 0){
                Snackbar.make(updateContractorBinding.linearLayout,"Cost per day can't be zero or negative",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else if(capacity.toInt() <= 0){
                Snackbar.make(updateContractorBinding.linearLayout,"Capacity can't be zero or negative",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
            else {
                updateContractorDetails()
            }
        }

        updateContractorBinding.buttonDeleteContractor.setOnClickListener {
            showAlertMessage()
        }

        return updateContractorBinding.root
    }

    private fun showAlertMessage() {
        val dialog = activity?.let { AlertDialog.Builder(it) }
        dialog?.setCancelable(false)
        dialog?.setTitle("Delete Contractor")
        dialog?.setMessage("Are you sure you want to delete this contractor ?")
        dialog?.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })
        dialog?.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            deleteContractor()
        })
        dialog?.create()?.show()
    }

    private fun deleteContractor() {
        reference.child(uid).removeValue().addOnCompleteListener { task ->
            if(task.isSuccessful){
                reference.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(ds in snapshot.children){
                            contractor = ds.getValue(Contractor::class.java)!!
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                clearAllTextArea()
                Snackbar.make(updateContractorBinding.linearLayout,"The contractor has been deleted",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            else{
                Snackbar.make(updateContractorBinding.linearLayout,"The deletion was not successful, Please try again!",Snackbar.LENGTH_LONG).setAction("close",View.OnClickListener { }).show()
            }
        }
    }

    private fun clearAllTextArea() {
        updateContractorBinding.textInputContractorName.setText("")
        updateContractorBinding.textInputContractorEmail.setText("")
        updateContractorBinding.textInputContractorPass.setText("")
        updateContractorBinding.textInputMessName.setText("")
        updateContractorBinding.textInputCostPerDay.setText("")
        updateContractorBinding.textInputFoodType.setText("")
        updateContractorBinding.textInputCapacity.setText("")
        updateContractorBinding.textInputAvailability.setText("")

    }

    private fun updateContractorDetails() {
        updateContractorBinding.buttonUpdateContractor.isClickable = false
        updateContractorBinding.buttonDeleteContractor.isClickable = false
        updateContractorBinding.progressBar.visibility = View.VISIBLE

        val map = mutableMapOf<String,Any>()
        map["contractorId"] = uid
        map["contractorEmail"] = updateContractorBinding.textInputContractorEmail.text.toString()
        map["contractorPassword"] = updateContractorBinding.textInputContractorPass.text.toString()
        map["userType"] = "Contractor"
        map["contractorName"] = updateContractorBinding.textInputContractorName.text.toString()
        map["costPerDay"] = updateContractorBinding.textInputCostPerDay.text.toString().toInt()
        map["messName"] = updateContractorBinding.textInputMessName.text.toString()
        map["foodType"] = updateContractorBinding.textInputFoodType.text.toString()
        map["capacity"] = updateContractorBinding.textInputCapacity.text.toString().toInt()

        val updatedAvailability = updateContractorBinding.textInputCapacity.text.toString().toInt() - slotFilled

        map["availability"] = updatedAvailability
//        map["feedbackReceived"] = adminViewModel.feedbackList
//        map["studentEnrolled"] = adminViewModel.studentEnrolledList

        reference.child(uid).updateChildren(map).addOnCompleteListener { task ->
            if(task.isSuccessful){
                retrieveDetailsFromDb()
                Snackbar.make(updateContractorBinding.linearLayout,"The contractor has been updated successfully", Snackbar.LENGTH_LONG).setAction("Close", View.OnClickListener { }).show()
            }
            updateContractorBinding.buttonUpdateContractor.isClickable = true
            updateContractorBinding.buttonDeleteContractor.isClickable = true
            updateContractorBinding.progressBar.visibility = View.INVISIBLE
        }

    }

    private fun retrieveDetailsFromDb() {
        reference.orderByChild("contractorId").equalTo(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    updateContractorBinding.textInputContractorName.setText(ds.child("contractorName").value.toString())
                    updateContractorBinding.textInputContractorEmail.setText(ds.child("contractorEmail").value.toString())
                    updateContractorBinding.textInputContractorPass.setText(ds.child("contractorPassword").value.toString())
                    updateContractorBinding.textInputMessName.setText(ds.child("messName").value.toString())
                    updateContractorBinding.textInputCostPerDay.setText(ds.child("costPerDay").value.toString())
                    updateContractorBinding.textInputFoodType.setText(ds.child("foodType").value.toString())
                    updateContractorBinding.textInputCapacity.setText(ds.child("capacity").value.toString())
                    updateContractorBinding.textInputAvailability.setText(ds.child("availability").value.toString())
                    slotFilled = ds.child("capacity").value.toString().toInt() - ds.child("availability").value.toString().toInt()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun receiveContractorDetails() {
        updateContractorBinding.textInputContractorName.setText(arguments?.getString("contractorName").toString())
        updateContractorBinding.textInputContractorEmail.setText(arguments?.getString("contractorEmail").toString())
        updateContractorBinding.textInputContractorPass.setText(arguments?.getString("contractorPassword").toString())
        updateContractorBinding.textInputMessName.setText(arguments?.getString("messName").toString())
        updateContractorBinding.textInputCostPerDay.setText(arguments?.getString("costPerDay").toString())
        updateContractorBinding.textInputFoodType.setText(arguments?.getString("foodType").toString())
        updateContractorBinding.textInputCapacity.setText(arguments?.getString("capacity").toString())
        updateContractorBinding.textInputAvailability.setText(arguments?.getString("availability").toString())
        uid = arguments?.getString("contractorId").toString()
        slotFilled = arguments?.getString("capacity").toString().toInt() - arguments?.getString("availability").toString().toInt()
    }

}