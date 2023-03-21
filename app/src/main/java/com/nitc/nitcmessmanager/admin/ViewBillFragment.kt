package com.nitc.nitcmessmanager.admin

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
import com.nitc.nitcmessmanager.databinding.FragmentViewBillBinding
import com.nitc.nitcmessmanager.model.Student

class ViewBillFragment : Fragment() {

    lateinit var mangeBillBinding: FragmentViewBillBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("students")
    val studentList = ArrayList<Student>()
    lateinit var studentBillAdapter: StudentBillAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mangeBillBinding = FragmentViewBillBinding.inflate(inflater,container,false)

        retrievePaymentDetailsFromDb()

        mangeBillBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mangeBillBinding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        }
        )

        return mangeBillBinding.root
    }

    private fun searchList(newText: String) {
        val searchList = ArrayList<Student>()
        for(student in studentList){
            if(student.studentRollNo.lowercase().contains(newText.lowercase())){
                searchList.add(student)
            }
        }
        studentBillAdapter.searchByRoll(searchList)
    }

    private fun retrievePaymentDetailsFromDb() {
        mangeBillBinding.progressBar.visibility = View.VISIBLE
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for(std in snapshot.children){
                    val student = std.getValue(Student::class.java)
                    if(student != null){
                        studentList.add(student)
                    }
                }
                if(studentList.isEmpty()){
                    mangeBillBinding.textViewNoContractorRegistered.visibility = View.VISIBLE
                    mangeBillBinding.search.visibility = View.INVISIBLE
                    mangeBillBinding.recyclerViewMessDueList.visibility = View.INVISIBLE
                }
                else {
                    studentList.reverse()
                    mangeBillBinding.recyclerViewMessDueList.layoutManager =
                        LinearLayoutManager(activity)
                    studentBillAdapter = StudentBillAdapter(studentList)
                    mangeBillBinding.recyclerViewMessDueList.adapter = studentBillAdapter
                }
                mangeBillBinding.progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}