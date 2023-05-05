package com.adz.mlimivisual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adz.mlimivisual.databinding.ActivityAgentBinding
import com.adz.mlimivisual.models.UserModel
import com.adz.mlimivisual.utils.AppUtil
import com.adz.mlimivisual.utils.preferences
import com.google.firebase.database.*

class AgentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgentBinding
    private var employee_id: EditText? = null
    private var password: EditText? = null
    private lateinit var databaseReference: DatabaseReference
    private var login: Button? = null
    private val appUtil = AppUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        employee_id = binding.Email055
        password = binding.Password055
        login = binding.loginButton

        login!!.setOnClickListener {
            databaseReference =
                FirebaseDatabase.getInstance().getReference("Users").child(appUtil.getUID()!!)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        val id = userModel?.employeeID.toString()
                        val input1 = employee_id!!.text.toString()
                        if(input1.isNotEmpty()){
                            if(id == input1){
                                preferences.setDataLogin(this@AgentActivity, true)
                                startActivity(
                                    Intent(
                                        this@AgentActivity,
                                        AdminActivity::class.java
                                    )
                                )
                            }else{
                                Toast.makeText(
                                    this@AgentActivity,
                                    "Matching gone wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }else{
                            Toast.makeText(
                                this@AgentActivity,
                                "Employee ID cant be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }else{
                        Toast.makeText(
                            this@AgentActivity,
                            "network security",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}