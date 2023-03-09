package com.nitc.nitcmessmanager.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitc.nitcmessmanager.databinding.ItemStudentBinding
import com.nitc.nitcmessmanager.model.Student

class StudentAdapter(val studentList : ArrayList<Student>) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>(){
    class StudentViewHolder(val adapterBinding : ItemStudentBinding) : RecyclerView.ViewHolder(adapterBinding.root){
        val name : TextView = adapterBinding.name
        val roll : TextView = adapterBinding.textViewRollNo
        val email : TextView = adapterBinding.textViewEmail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.adapterBinding.name.text = studentList[position].studentName
        holder.adapterBinding.textViewRollNo.text = studentList[position].studentRollNo
        holder.adapterBinding.textViewEmail.text = studentList[position].studentEmail
    }
}