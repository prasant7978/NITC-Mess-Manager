package com.nitc.nitcmessmanager.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.databinding.ItemMessListBinding
import com.nitc.nitcmessmanager.model.Contractor

class MessAdapter(private var messList : ArrayList<Contractor>) : RecyclerView.Adapter<MessAdapter.MessViewHolder>() {
    class MessViewHolder(val adapterBinding : ItemMessListBinding) : RecyclerView.ViewHolder(adapterBinding.root){
        val messName : TextView = adapterBinding.messName
        val foodType : TextView = adapterBinding.foodType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessViewHolder {
        val binding = ItemMessListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MessViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messList.size
    }

    override fun onBindViewHolder(holder: MessViewHolder, position: Int) {
        holder.adapterBinding.messName.text = messList[position].messName
        holder.adapterBinding.foodType.text = messList[position].foodType
        holder.adapterBinding.constraintLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("messName",messList[position].messName)
            bundle.putString("foodType",messList[position].foodType)
            bundle.putString("costPerDay",messList[position].costPerDay.toString())
            bundle.putString("availability",messList[position].availability.toString())
            bundle.putString("contractorId",messList[position].contractorId)

            val selectMessFragment = SelectMessFragment()
            selectMessFragment.arguments = bundle

            val appCompatActivity = it.context as AppCompatActivity
            appCompatActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout,selectMessFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun searchByFoodType(searchList : ArrayList<Contractor>){
        messList = searchList
        notifyDataSetChanged()
    }
}