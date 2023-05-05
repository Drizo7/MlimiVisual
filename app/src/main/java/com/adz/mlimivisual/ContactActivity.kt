package com.adz.mlimivisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.adz.mlimivisual.fragments.ContactFragment


class ContactActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        loadFragment(ContactFragment())

        navController = findNavController(R.id.fragmentContactView)
        navController.navigate(R.id.contactFragment)
    }


    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContactView,fragment)
        transaction.commit()
    }
    override fun onBackPressed() {
        val i = Intent(applicationContext, MainActivity::class.java)
        startActivity(i)
    }
}