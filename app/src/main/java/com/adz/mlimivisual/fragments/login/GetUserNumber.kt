package com.adz.mlimivisual.fragments.login

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.adz.mlimivisual.R
import com.adz.mlimivisual.databinding.FragmentGetUserNumberBinding
import com.adz.mlimivisual.fragments.GetUserData
import com.adz.mlimivisual.models.UserModel
import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class GetUserNumber : Fragment() {

    private var number: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var code: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    lateinit var binding: FragmentGetUserNumberBinding
    private lateinit var cirLoader: CircularDotsLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View {
            binding = FragmentGetUserNumberBinding.inflate(layoutInflater, container, false)

        cirLoader = binding.cirLoader
        cirLoader.visibility = View.GONE
        cirLoader.defaultColor = ContextCompat.getColor(requireContext(), R.color.colorGrey)
        cirLoader.selectedColor = ContextCompat.getColor(requireContext(), R.color.colorGreyLt)
        cirLoader.bigCircleRadius = 42
        cirLoader.radius = 11
        cirLoader.animDur = 100
        cirLoader.firstShadowColor = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        cirLoader.secondShadowColor = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        cirLoader.showRunningShadow = true

        if (container != null) {
            container.removeAllViews()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding.btnGenerateOTP.setOnClickListener {
            try {
                cirLoader.visibility = View.VISIBLE
                if (checkNumber()) {
                    val phoneNumber = binding.countryCodePicker.selectedCountryCodeWithPlus + number
                    sendCode(phoneNumber)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                cirLoader.visibility = View.VISIBLE
                firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userModel =
                            UserModel(
                                "", "", "",
                                firebaseAuth!!.currentUser!!.phoneNumber!!,
                                firebaseAuth!!.uid!!
                            )

                        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(userModel)
                        cirLoader.visibility = View.VISIBLE

                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.fragmentContainerView, GetUserData())
                            ?.commit()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                else if (e is FirebaseTooManyRequestsException)
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationCode: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                code = verificationCode
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, VerifiyNumber.newInstance(code!!))
                    ?.commit()
            }
        }

            return binding.root
    }


    private fun sendCode(phoneNumber: String) {
        try{
            cirLoader.visibility = View.VISIBLE
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            callbacks

        )
            cirLoader.visibility = View.VISIBLE
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkNumber(): Boolean {

        number = binding.edtNumber.text.toString().trim()
        if (number!!.isEmpty()) {
            return false
        } else if (number!!.length > 10) {
             return false
        } else return true
    }
}