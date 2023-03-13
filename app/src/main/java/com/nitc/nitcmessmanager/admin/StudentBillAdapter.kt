package com.nitc.nitcmessmanager.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitc.nitcmessmanager.databinding.ItemStudentBillBinding
import com.nitc.nitcmessmanager.model.Student

class StudentBillAdapter(private var dueList: ArrayList<Student>) : RecyclerView.Adapter<StudentBillAdapter.StudentBillViewHolder>(){

    class StudentBillViewHolder(val adapterBinding : ItemStudentBillBinding) : RecyclerView.ViewHolder(adapterBinding.root){
        val name : TextView = adapterBinding.studentName
        val roll : TextView = adapterBinding.studentRoll
        val due : TextView = adapterBinding.messDue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentBillViewHolder {
        val binding = ItemStudentBillBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentBillViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dueList.size
    }

    override fun onBindViewHolder(holder: StudentBillViewHolder, position: Int) {
        holder.adapterBinding.studentName.text = dueList[position].studentName
        holder.adapterBinding.studentRoll.text = dueList[position].studentRollNo
        holder.adapterBinding.messDue.text = dueList[position].messBill.toString()
        if(dueList[position].messEnrolled == "")
            holder.adapterBinding.messEnrolled.text = "Not Enrolled yet"
        else
            holder.adapterBinding.messEnrolled.text = dueList[position].messEnrolled
    }

    fun searchByRoll(searchList : ArrayList<Student>){
        dueList = searchList
        notifyDataSetChanged()
    }
}