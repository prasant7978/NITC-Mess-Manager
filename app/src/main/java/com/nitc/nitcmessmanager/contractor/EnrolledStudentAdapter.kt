package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.ItemEnrolledStudentBinding
import com.nitc.nitcmessmanager.model.Student

class EnrolledStudentAdapter(private var studentList : ArrayList<Student>) : RecyclerView.Adapter<EnrolledStudentAdapter.EnrolledStudentViewHolder>(){
    class EnrolledStudentViewHolder(val adapterBinding : ItemEnrolledStudentBinding) : RecyclerView.ViewHolder(adapterBinding.root){
        val name : TextView = adapterBinding.studentName
        val roll : TextView = adapterBinding.studentRoll
        val mail : TextView = adapterBinding.studentEmail
        val due : TextView = adapterBinding.messDue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrolledStudentViewHolder {
        val binding = ItemEnrolledStudentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EnrolledStudentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: EnrolledStudentViewHolder, position: Int) {
        holder.adapterBinding.studentName.text = studentList[position].studentName
        holder.adapterBinding.studentRoll.text = studentList[position].studentRollNo
        holder.adapterBinding.studentEmail.text = studentList[position].studentEmail
        holder.adapterBinding.messDue.text = studentList[position].messBill.toString()
    }

    fun searchByRoll(searchList : ArrayList<Student>){
        studentList = searchList
        notifyDataSetChanged()
    }
}