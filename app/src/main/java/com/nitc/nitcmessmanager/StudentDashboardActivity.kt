package com.nitc.nitcmessmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.R
import com.nitc.nitcmessmanager.databinding.ActivityStudentDashboardBinding

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var dashboardBinding: ActivityStudentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardBinding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        val view = dashboardBinding.root

        setContentView(view)


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