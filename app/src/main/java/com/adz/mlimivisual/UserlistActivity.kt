package com.adz.mlimivisual

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adz.mlimivisual.databinding.ActivityUserlistBinding
import com.adz.mlimivisual.models.AppointmentModel
import com.google.firebase.database.*

class UserlistActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<AppointmentModel>
    lateinit var binding : ActivityUserlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRecyclerview = binding.userList
        userRecyclerview.layoutManager = LinearLayoutManager(this)

        userRecyclerview.setHasFixedSize(true)

        userArrayList = arrayListOf()
        getUserData()

        userRecyclerview.setOnClickListener{

        }
    }

    private fun getUserData() {

        dbref = FirebaseDatabase.getInstance().getReference("Appointment")

        dbref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                /*var user: AppointmentModel? = null // Define the user variable outside the for loop
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        user = userSnapshot.getValue(AppointmentModel::class.java)
                        userArrayList.add(user!!)
                    }
                    userRecyclerview.adapter = MyAdapter(userArrayList)
                    Toast.makeText(this, "User: " + user, Toast.LENGTH_SHORT).show() // Display the user variable in a toast message
                }*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

}
