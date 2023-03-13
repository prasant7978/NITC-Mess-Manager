package com.nitc.nitcmessmanager.contractor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nitc.nitcmessmanager.R
import com.nitc.nitcmessmanager.authentication.LoginActivity
import com.nitc.nitcmessmanager.databinding.ActivityContractorDashboardBinding

class ContractorDashboard : AppCompatActivity() {

    lateinit var contractorDashboardBinding: ActivityContractorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contractorDashboardBinding = ActivityContractorDashboardBinding.inflate(layoutInflater)
        val view = contractorDashboardBinding.root

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
            val intent = Intent(this@ContractorDashboard, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return true
    }
}