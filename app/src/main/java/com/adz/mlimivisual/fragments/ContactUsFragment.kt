package com.adz.mlimivisual.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.adz.mlimivisual.Enquiry
import com.adz.mlimivisual.databinding.FragmentContactUsBinding


class ContactUsFragment : Fragment() {
    private var binding: FragmentContactUsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentContactUsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        binding!!.enquiry.setOnClickListener {
            val i = Intent(requireContext(), Enquiry::class.java)
            startActivity(i)
        }

        binding!!.emergency.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+265884862658"))
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(intent)
            }
        }

        return fragmentBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
