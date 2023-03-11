package com.nitc.nitcmessmanager.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentManageStudentBinding
import com.nitc.nitcmessmanager.model.Student


class ManageStudentFragment : Fragment() {

    lateinit var manageStudentBinding: FragmentManageStudentBinding
    val studentList = ArrayList<Student>()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("students")
    lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        manageStudentBinding = FragmentManageStudentBinding.inflate(inflater, container, false)

        retrieveStudentListFromDb()

        manageStudentBinding.textViewAddStudent.setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
            val addStudentFragment = AddStudentFragment()

            fragmentTransaction.replace(R.id.frameLayout,addStudentFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        manageStudentBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchList(newText)
                    return true
                }
            }
        )

        return manageStudentBinding.root
    }

    fun searchList(text : String){
        val searchList = ArrayList<Student>()
        for(student in studentList){
            if(student.studentRollNo.lowercase().contains(text.lowercase())){
                searchList.add(student)
            }
        }
        studentAdapter.searchByRoll(searchList)
    }

    private fun retrieveStudentListFromDb(){
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for(std in snapshot.children){
                    val student = std.getValue(Student::class.java)
                    if(student != null){
                        studentList.add(student)
                    }
                }
                studentList.reverse()
                manageStudentBinding.recyclerViewStudentList.layoutManager = LinearLayoutManager(activity)
                studentAdapter = StudentAdapter(studentList)
                manageStudentBinding.recyclerViewStudentList.adapter = studentAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}