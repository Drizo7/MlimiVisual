package com.adz.mlimivisual.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adz.mlimivisual.R
import com.adz.mlimivisual.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    var mainActivityInterface: MainActivityInterface? = null

    interface MainActivityInterface {
        fun currentUserExist(): Boolean
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        if (container != null) {
            container.removeAllViews()
        }

        binding!!.btnGenerateOTP.setOnClickListener {
            if (mainActivityInterface?.currentUserExist() == true) {
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.setReorderingAllowed(true)
                    ?.replace(R.id.fragmentContainerView, FlavorFragment())
                    ?.commit()
            }
        }

        return fragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityInterface) {
            mainActivityInterface = context
        } else {
            throw RuntimeException("$context must implement MainActivityInterface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}