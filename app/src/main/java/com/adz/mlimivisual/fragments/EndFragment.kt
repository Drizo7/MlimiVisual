package com.adz.mlimivisual.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.adz.mlimivisual.R
import com.adz.mlimivisual.databinding.FragmentEndBinding

class EndFragment : Fragment() {

    private var binding: FragmentEndBinding? = null

    private val sharedViewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentEndBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        if (container != null) {
            container.removeAllViews()
        }
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Specify the fragment as the lifecycle owner
             lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
             viewModel = sharedViewModel

            // Assign the fragment
             endFragment = this@EndFragment
        }
    }

    /**
     * Navigate to the next screen to choose pickup date.
     */
    fun goToNextScreen() {
        findNavController().navigate(R.id.action_EndFragment_to_SummaryFragment)
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