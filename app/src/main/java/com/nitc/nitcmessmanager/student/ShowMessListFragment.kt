package com.nitc.nitcmessmanager.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.admin.ContractorAdapter
import com.nitc.nitcmessmanager.databinding.FragmentShowMessListBinding
import com.nitc.nitcmessmanager.model.Contractor

class ShowMessListFragment : Fragment() {

    lateinit var showMessListBinding: FragmentShowMessListBinding
    val messList = ArrayList<Contractor>()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("contractors")
    lateinit var messAdapter: MessAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showMessListBinding = FragmentShowMessListBinding.inflate(inflater,container,false)

        retrieveMessDetailsFromDb()

        showMessListBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showMessListBinding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        }
        )

        return showMessListBinding.root
    }

    fun searchList(text : String){
        val searchList = ArrayList<Contractor>()
        for(contractor in messList){
            if(contractor.foodType.lowercase().contains(text.lowercase())){
                searchList.add(contractor)
            }
        }
        messAdapter.searchByFoodType(searchList)
    }

    private fun retrieveMessDetailsFromDb() {
        showMessListBinding.progressBar.visibility = View.VISIBLE
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messList.clear()
                for(ds in snapshot.children){
                    val mess = ds.getValue(Contractor::class.java)
                    if(mess != null){
                        messList.add(mess)
                    }
                }

                if(messList.isEmpty()){
                    showMessListBinding.textViewAvailableMess.visibility = View.INVISIBLE
                    showMessListBinding.textViewNoContractorRegistered.visibility = View.VISIBLE
                    showMessListBinding.search.visibility = View.INVISIBLE
                }
                else {
                    messList.reverse()
                    showMessListBinding.recyclerViewMessList.layoutManager =
                        LinearLayoutManager(activity)
                    messAdapter = MessAdapter(messList)
                    showMessListBinding.recyclerViewMessList.adapter = messAdapter
                }
                showMessListBinding.progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}