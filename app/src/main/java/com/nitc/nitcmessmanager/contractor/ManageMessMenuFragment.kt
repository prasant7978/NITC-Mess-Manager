package com.nitc.nitcmessmanager.contractor

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nitc.nitcmessmanager.databinding.FragmentManageMessMenuBinding
import com.nitc.nitcmessmanager.model.Contractor
import com.nitc.nitcmessmanager.model.Menu
import com.nitc.nitcmessmanager.model.Student

class ManageMessMenuFragment : Fragment() {

    lateinit var manageMessMenuBinding: FragmentManageMessMenuBinding
    val db : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference = db.reference.child("contractors")
    var days  : Array<String> = arrayOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    var previousSelectedIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        manageMessMenuBinding = FragmentManageMessMenuBinding.inflate(inflater,container,false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val userType = arguments?.getString("userType")
        var messName = ""

        if(userType == "Student"){
            manageMessMenuBinding.textInputBreakfastDetails.isClickable = false
            manageMessMenuBinding.textInputBreakfastDetails.isEnabled = false
            manageMessMenuBinding.textInputLunchDetails.isClickable = false
            manageMessMenuBinding.textInputLunchDetails.isEnabled = false
            manageMessMenuBinding.textInputDinnerDetails.isClickable = false
            manageMessMenuBinding.textInputDinnerDetails.isEnabled = false
            manageMessMenuBinding.buttonUpdateMenu.visibility = View.GONE

            messName = arguments?.getString("messName").toString()

//            Log.d("menu",userType)
//            Log.d("menu",messName)

            fetchMessMenuForStudent(messName, days[previousSelectedIndex])
        }
        else {
            fetchMessMenu(uid, days[previousSelectedIndex])
        }

        manageMessMenuBinding.buttonSelectDay.setOnClickListener {
            val dayBuilder = AlertDialog.Builder(context)
            dayBuilder.setTitle("Select day")
                .setSingleChoiceItems(days,previousSelectedIndex) { dialog, selectedIndex ->
                    val selectedDay = days[selectedIndex]
                    previousSelectedIndex = selectedIndex
//                    manageMessMenuBinding.buttonSelectDay.text = selectedDay
                    manageMessMenuBinding.textViewSelectedDay.text = selectedDay
                    if(userType == "Student"){
                        fetchMessMenuForStudent(messName, days[previousSelectedIndex])
                    }
                    else {
                        fetchMessMenu(uid, selectedDay)
                    }

                    dialog.dismiss()
                }
                .setPositiveButton("Ok"){dialog, which->
                    val selectedDay = days[0]
                    manageMessMenuBinding.buttonSelectDay.text = selectedDay

                    if(userType == "Student"){
                        fetchMessMenuForStudent(messName, days[previousSelectedIndex])
                    }
                    else {
                        fetchMessMenu(uid, selectedDay)
                    }

                    dialog.dismiss()
                }
                .create().show()
        }

        manageMessMenuBinding.buttonUpdateMenu.setOnClickListener {
            val breakfast = manageMessMenuBinding.textInputBreakfastDetails.text.toString()
            val lunch = manageMessMenuBinding.textInputLunchDetails.text.toString()
            val dinner = manageMessMenuBinding.textInputDinnerDetails.text.toString()

            if(breakfast.isEmpty() || lunch.isEmpty() || dinner.isEmpty()){
                Toast.makeText(activity,"Please enter all menu details",Toast.LENGTH_SHORT).show()
            }
            else{
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Update Mess Menu")
                dialog.setMessage("Are you sure, want to update mess menu for $days[previousSelectedIndex] ?")
                dialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                dialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    updateMessMenu(uid,breakfast,lunch,dinner,days[previousSelectedIndex],previousSelectedIndex)
                })
            }
        }

        return manageMessMenuBinding.root
    }

    private fun updateMessMenu(uid: String, breakfast: String, lunch: String, dinner: String, selectedDay: String,dayIndex:Int) {
        reference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val contractor = snapshot.getValue(Contractor::class.java)
                if(contractor != null){
//                    Log.d("menu","contractor found")
                    var flag = false

                    val menuList = contractor.messMenu

                    for(menu in menuList){
                        if(menu.day == selectedDay){
                            flag = true
                            menu.breakfast = breakfast
                            menu.lunch = lunch
                            menu.dinner = dinner
//                            Log.d("menu", menu.day)
//                            Log.d("menu", menu.breakfast)
//                            Log.d("menu", menu.lunch)
//                            Log.d("menu", menu.dinner)
                            break
                        }
                    }

                    if(!flag)
                        reference.child(uid).child("messMenu").child("$dayIndex").setValue(Menu(selectedDay,breakfast,lunch,dinner))
                    else {
                        contractor.messMenu = menuList
                        reference.child(contractor.contractorId).setValue(contractor)
                    }

                }
//                Log.d("menu",uid)
//                Log.d("menu",selectedDay)
                fetchMessMenu(uid,selectedDay)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchMessMenu(uid : String, selectedDay: String) {
        manageMessMenuBinding.textInputBreakfastDetails.setText("")
        manageMessMenuBinding.textInputLunchDetails.setText("")
        manageMessMenuBinding.textInputDinnerDetails.setText("")

        reference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val contractor = snapshot.getValue(Contractor::class.java)
                if(contractor != null){
                    val menuList = contractor.messMenu
                    if(menuList.isNotEmpty()) {
                        for (menu in menuList) {
                            if (menu.day == selectedDay) {
                                manageMessMenuBinding.textInputBreakfastDetails.setText(menu.breakfast)
                                manageMessMenuBinding.textInputLunchDetails.setText(menu.lunch)
                                manageMessMenuBinding.textInputDinnerDetails.setText(menu.dinner)
                                break
                            }
                        }
                    }
                    else{
                        manageMessMenuBinding.textInputBreakfastDetails.setText("")
                        manageMessMenuBinding.textInputLunchDetails.setText("")
                        manageMessMenuBinding.textInputDinnerDetails.setText("")
                        Toast.makeText(activity,"You don't have any mess menu",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchMessMenuForStudent(messName : String, selectedDay: String) {
        manageMessMenuBinding.textInputBreakfastDetails.setText("")
        manageMessMenuBinding.textInputLunchDetails.setText("")
        manageMessMenuBinding.textInputDinnerDetails.setText("")

        reference.orderByChild("messName").equalTo(messName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val contractor = ds.getValue(Contractor::class.java)
                    if(contractor != null) {
                        val menuList = contractor.messMenu
                        if (menuList.isNotEmpty()) {
                            for (menu in menuList) {
                                if (menu.day == selectedDay) {
                                    manageMessMenuBinding.textInputBreakfastDetails.setText(menu.breakfast)
                                    manageMessMenuBinding.textInputLunchDetails.setText(menu.lunch)
                                    manageMessMenuBinding.textInputDinnerDetails.setText(menu.dinner)
                                    break
                                }
                            }
                        } else {
                            manageMessMenuBinding.textInputBreakfastDetails.setText("")
                            manageMessMenuBinding.textInputLunchDetails.setText("")
                            manageMessMenuBinding.textInputDinnerDetails.setText("")
                            Toast.makeText(
                                activity,
                                "No menu have been added for $selectedDay",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}