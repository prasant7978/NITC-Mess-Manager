package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.FragmentEnrolledStudentListBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Feedback
import com.nitc.nitcmessmanager.model.Student

class EnrolledStudentListFragment : Fragment() {

    lateinit var enrolledStudentListBinding: FragmentEnrolledStudentListBinding
    var studentList = ArrayList<Student>()
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = db.reference.child("contractors")
    lateinit var studentAdapter: EnrolledStudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        enrolledStudentListBinding = FragmentEnrolledStudentListBinding.inflate(inflater,container,false)

        retrieveEnrolledStudentListFromDb()

        enrolledStudentListBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                enrolledStudentListBinding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        return enrolledStudentListBinding.root
    }

    private fun searchList(newText: String) {
        val searchList = ArrayList<Student>()
        for(student in studentList){
            if(student.studentRollNo.lowercase().contains(newText.lowercase())){
                searchList.add(student)
            }
        }
        studentAdapter.searchByRoll(searchList)
    }

    private fun retrieveEnrolledStudentListFromDb() {
        enrolledStudentListBinding.progressBar.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        ref.orderByChild("contractorId").equalTo(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for(fd in snapshot.children){
                    val contractor = fd.getValue(Contractor::class.java)
                    val student = contractor?.studentEnrolled
                    if(student?.isNotEmpty() == true)
                        studentList = student
                }

                if(studentList.isEmpty()){
                    enrolledStudentListBinding.search.visibility = View.INVISIBLE
                    enrolledStudentListBinding.recyclerView.visibility = View.INVISIBLE
                    enrolledStudentListBinding.textViewNoStudentEnrolled.visibility = View.VISIBLE
                }
                else {
                    studentList.reverse()
                    enrolledStudentListBinding.recyclerView.layoutManager =
                        LinearLayoutManager(activity)
                    studentAdapter = EnrolledStudentAdapter(studentList)
                    enrolledStudentListBinding.recyclerView.adapter = studentAdapter
                }
                enrolledStudentListBinding.progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}