package com.nitc.nitcmessmanager.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentManageContractorBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Student

class ManageContractorFragment : Fragment() {

    lateinit var manageContractorBinding: FragmentManageContractorBinding
    val contractorList = ArrayList<Contractor>()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("contractors")
    lateinit var contractorAdapter: ContractorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        manageContractorBinding = FragmentManageContractorBinding.inflate(inflater,container,false)

        retrieveContractorListFromDb()

        manageContractorBinding.textViewAddContractor.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val addContractorFragment = AddContractorFragment()

            fragmentTransaction.replace(R.id.frameLayout,addContractorFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        manageContractorBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                manageContractorBinding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        }
        )

        return manageContractorBinding.root
    }

    fun searchList(text : String){
        val searchList = ArrayList<Contractor>()
        for(contractor in contractorList){
            if(contractor.messName.lowercase().contains(text.lowercase())){
                searchList.add(contractor)
            }
        }
        contractorAdapter.searchByFoodType(searchList)
    }

    private fun retrieveContractorListFromDb() {
        manageContractorBinding.progressBar.visibility = View.VISIBLE
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contractorList.clear()
                for(std in snapshot.children){
                    val contractor = std.getValue(Contractor::class.java)
                    if(contractor != null){
                        contractorList.add(contractor)
                    }
                }

                if(contractorList.isEmpty()){
                    manageContractorBinding.textViewNoContractorRegistered.visibility = View.VISIBLE
                    manageContractorBinding.search.visibility = View.VISIBLE
                    manageContractorBinding.recyclerViewContractorList.visibility = View.VISIBLE
                }
                else {
                    contractorList.reverse()
                    manageContractorBinding.recyclerViewContractorList.layoutManager =
                        LinearLayoutManager(activity)
                    contractorAdapter =
                        ContractorAdapter(contractorList, this@ManageContractorFragment)
                    manageContractorBinding.recyclerViewContractorList.adapter = contractorAdapter
                }
                manageContractorBinding.progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}