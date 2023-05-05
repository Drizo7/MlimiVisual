package com.adz.mlimivisual.fragments

import android.annotation.SuppressLint
import android.content.Intent
import com.adz.mlimivisual.databinding.FragmentContactBinding
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adz.mlimivisual.AboutActivity
import com.adz.mlimivisual.Constants.AppConstants
import com.adz.mlimivisual.ContactAdapter
import com.adz.mlimivisual.Permission.AppPermission
import com.adz.mlimivisual.models.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber


class ContactFragment : Fragment() {
    private lateinit var appPermission: AppPermission
    private lateinit var fragmentContactBinding: FragmentContactBinding
    private lateinit var mobileContacts: ArrayList<UserModel>
    private lateinit var appContacts: ArrayList<UserModel>
    private var contactAdapter: ContactAdapter? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneNumber: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContactBinding = FragmentContactBinding.inflate(inflater, container, false)
        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        try {
            if (firebaseAuth != null && firebaseAuth.currentUser != null) {
                phoneNumber = firebaseAuth.currentUser!!.displayName ?: ""
            } else {
                Timber.tag("Contacts").e("Allow this app to access contacts ")
            }

            if (appPermission.isContactOk(requireContext())) {
                getAppContact2()
            } else {
                appPermission.requestContactPermission(requireActivity())
            }
        } catch (e: Exception) {
            Timber.tag("Contacts").e("An error occurred while trying to access contacts: ${e.message}")
            e.printStackTrace()
        }

        fragmentContactBinding.contactSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (contactAdapter != null)
                    contactAdapter!!.filter.filter(newText)
                return false
            }
        })
        fragmentContactBinding.contactSearchView.setOnClickListener {
            //getMobileContact()
        }

        return fragmentContactBinding.root
    }


    @SuppressLint("Range")
    private fun getMobileContact() {
        try {
            val projection = arrayOf(
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val contentResolver = requireContext().contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
        null,
        null,
        null
    )

    if (cursor != null) {
        Timber.tag("TAG").d("Number of rows: %s", cursor.count)
        mobileContacts = ArrayList()
        while (cursor.moveToNext()) {

            val name =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var number =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            number = number.replace("\\s".toRegex(), "")
            val num = number.elementAt(0).toString()
            if (num == "0")
                number = number.replaceFirst("(?:0)+".toRegex(), "+92")
            val userModel = UserModel()
            userModel.name = name
            userModel.number = number
            mobileContacts.add(userModel)
        }
        cursor.close()
        //displayMobileContacts(mobileContacts)
        getAppContact1(mobileContacts)
    }
}
        catch (e: Exception) {
            Timber.tag("Contacts").e(e, "Error while retrieving contacts")
        }
    }
    private fun displayMobileContacts(contacts: ArrayList<UserModel>) {
        fragmentContactBinding.recyclerViewContact.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            contactAdapter = ContactAdapter(contacts)
            adapter = contactAdapter
        }
    }

    private fun getAppContact(mobileContact: ArrayList<UserModel>) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference.orderByChild("number")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appContacts = ArrayList()
                    for (data in snapshot.children) {

                        val number = data.child("number").value.toString()
                        for (mobileModel in mobileContact) {
                            if (mobileModel.number == number && number != phoneNumber) {
                                val userModel = data.getValue(UserModel::class.java)
                                appContacts.add(userModel!!)
                            }
                        }
                    }

                    fragmentContactBinding.recyclerViewContact.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        contactAdapter = ContactAdapter(appContacts)
                        adapter = contactAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun getAppContact2() {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference.orderByChild("number")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appContacts = ArrayList()
                    for (data in snapshot.children) {

                        val number = data.child("number").value.toString()
                        val name = data.child("name").value.toString()
                        if (number != phoneNumber) {
                            //val userModel = data.getValue(UserModel::class.java)
                            val userModel =  data.getValue(UserModel::class.java)
                            appContacts.add(userModel!!)
                        }
                    }

                    fragmentContactBinding.recyclerViewContact.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        contactAdapter = ContactAdapter(appContacts)
                        adapter = contactAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getAppContact1(mobileContact: ArrayList<UserModel>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference.orderByChild("number")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appContacts = ArrayList()
                    for (data in snapshot.children) {
                        val number = data.child("number").value.toString()
                        for (mobileModel in mobileContact) {
                            if (mobileModel.number == number) {
                                appContacts.add(mobileModel)
                                break
                            }
                        }
                    }

                    fragmentContactBinding.recyclerViewContact.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        contactAdapter = ContactAdapter(appContacts)
                        adapter = contactAdapter
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.CONTACT_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //getMobileContact()
                getAppContact2()
                else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

