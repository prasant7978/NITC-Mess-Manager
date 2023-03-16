package com.nitc.nitcmessmanager.contractor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.admin.UpdateStudentFragment
import com.nitc.nitcmessmanager.databinding.ItemFeedbackBinding
import com.nitc.nitcmessmanager.model.Feedback

class FeedbackAdapter(private var feedbackList : ArrayList<Feedback>) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>(){
    class FeedbackViewHolder(val adapterBinding : ItemFeedbackBinding) : RecyclerView.ViewHolder(adapterBinding.root){
        val name : TextView = adapterBinding.textViewFeedbackStudentName
        val msg : TextView = adapterBinding.textViewFeedbackMessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeedbackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.adapterBinding.textViewFeedbackStudentName.text = feedbackList[position].studentName
        holder.adapterBinding.textViewFeedbackMessage.text = feedbackList[position].feedbackMessage
        holder.adapterBinding.constraintLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("studentName",feedbackList[position].studentName)
            bundle.putString("feedbackMessage",feedbackList[position].feedbackMessage)
            bundle.putString("feedbackId",feedbackList[position].feedbackId)

            val singleFeedbackFragment = SingleFeedbackFragment()
            singleFeedbackFragment.arguments = bundle

            val appCompatActivity = it.context as AppCompatActivity
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout,singleFeedbackFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}