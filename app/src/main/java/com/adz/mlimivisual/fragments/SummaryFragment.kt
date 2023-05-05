package com.adz.mlimivisual.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.adz.mlimivisual.databinding.FragmentSummaryBinding
import com.adz.mlimivisual.models.AppointmentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SummaryFragment : Fragment() {
    private var binding: FragmentSummaryBinding? = null
    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null


    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        if (container != null) {
            container.removeAllViews()
        }
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            summaryFragment = this@SummaryFragment
        }



    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     */
    fun sendOrder() {
        Toast.makeText(activity, "Sending Order", Toast.LENGTH_SHORT).show()

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Appointment")

        val input1 = binding!!.description.text.toString()
        val input2 = binding!!.location.text.toString()
        val input3 = binding!!.flavor.text

        val aptModel =
            AppointmentModel(
                input1, input2, input3 as String,
                firebaseAuth!!.currentUser!!.displayName!!,
                firebaseAuth!!.currentUser!!.phoneNumber!!,
                firebaseAuth!!.uid!!
            )

        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(aptModel)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}