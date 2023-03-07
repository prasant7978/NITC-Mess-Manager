package com.nitc.nitcmessmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.R
import com.nitc.nitcmessmanager.databinding.ActivityStudentDashboardBinding

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var dashboardBinding: ActivityStudentDashboardBinding
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref = db.reference.child("students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardBinding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        val view = dashboardBinding.root

        setContentView(view)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        ref.orderByChild("studentId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    Log.d("debug",ds.child("studentName").value.toString())
                    dashboardBinding.textViewName.text = ds.child("studentName").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dashboardBinding.studentProfile.setOnClickListener {
            val intent = Intent(this@StudentDashboardActivity,StudentUpdateProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_signout,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.signOut){
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,"Sign out is successfull", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@StudentDashboardActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return true
    }
}