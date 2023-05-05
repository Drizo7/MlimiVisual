package com.adz.mlimivisual.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adz.mlimivisual.R
import com.adz.mlimivisual.databinding.FragmentVerifiyNumberBinding
import com.adz.mlimivisual.fragments.GetUserData
import com.adz.mlimivisual.models.UserModel
import com.chaos.view.PinView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class VerifiyNumber : Fragment() {

    private var code: String? = null
    private lateinit var pin: String
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    lateinit var binding: FragmentVerifiyNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("Code")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View {
        binding = FragmentVerifiyNumberBinding.inflate(layoutInflater, container, false)

        if (container != null) {
            container.removeAllViews()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding.btnVerify.setOnClickListener {
            if (checkPin()) {
                val credential = PhoneAuthProvider.getCredential(code!!, pin)
                signInUser(credential)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, GetUserData())
                    .commit()
                /*startActivity(Intent(context, GetUserData::class.java))
                requireActivity().finish()*/
            }
        }
        return binding.root
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val userModel =
                    UserModel(
                        "", "", "",
                        firebaseAuth!!.currentUser!!.phoneNumber!!,
                        firebaseAuth!!.uid!!
                    )
                databaseReference!!.child(firebaseAuth?.uid!!).setValue(userModel)
                /*requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, GetUserData())
                    .commit()*/
                /*startActivity(Intent(context, GetUserData::class.java))
                requireActivity().finish()*/
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(code: String) =
            VerifiyNumber().apply {
                arguments = Bundle().apply {
                    putString("Code", code)
                }
            }
    }

    @SuppressLint("CutPasteId")
    private fun checkPin(): Boolean {
        pin = requireView().findViewById<PinView>(R.id.otp_text_view).text.toString()
        if (pin.isEmpty()) {
            requireView().findViewById<PinView>(R.id.otp_text_view).error = "Filed is required"
            return false
        } else if (pin.length < 6) {
            requireView().findViewById<PinView>(R.id.otp_text_view).error = "Enter valid pin"
            return false
        } else return true
    }
}