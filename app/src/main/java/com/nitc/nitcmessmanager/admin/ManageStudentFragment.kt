package com.nitc.nitcmessmanager.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        return manageStudentBinding.root
    }

    private fun retrieveStudentListFromDb(){
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
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