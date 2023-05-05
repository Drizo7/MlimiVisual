package com.adz.mlimivisual.fragments.login

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.adz.mlimivisual.R
import com.adz.mlimivisual.SharedViewModel
import com.adz.mlimivisual.databinding.FragmentLoginBinding
import com.adz.mlimivisual.models.Country
import com.adz.mlimivisual.utils.*
import com.adz.mlimivisual.views.CustomProgressView


class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    private var country: Country? = null
    private var progressView: CustomProgressView?=null
    private val viewModel: LogInViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    //private val sharedViewModel by activityViewModels<SharedViewModel>()
    //private val fooViewModel: FooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressView = CustomProgressView(requireContext())
        setDataInView()
        subscribeObservers()
    }

    private fun setDataInView() {
        //binding.viewmodel = viewModel
        binding.apply {
            viewmodel = viewModel

        }
        setDefaultCountry()
        binding.txtCountryCode.setOnClickListener {
            closeKeyBoard(requireActivity())
            findNavController().navigate(R.id.action_loginFragment_to_FCountries)
        }
        binding.btnGetOtp.setOnClickListener {
            validate()
        }
    }

    fun closeKeyBoard(activity: Activity){
        val view=activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun validate() {
        try {
            closeKeyBoard(requireActivity())
            val mobileNo = viewModel.mobile.value?.trim()
            val country = viewModel.country.value
            when {
                Validator.isMobileNumberEmpty(mobileNo) -> snack(requireActivity(), "Enter valid mobile number")
                country == null -> snack(requireActivity(), "Select a country")
                !Validator.isValidNo(country.code, mobileNo!!) -> snack(
                    requireActivity(),
                    "Enter valid mobile number"
                )
                isNoInternet(requireContext()) -> snackNet(requireActivity())
                else -> {
                    viewModel.setMobile()
                    viewModel.setProgress(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isNetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            (capabilities != null &&
                    ((capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            (activeNetworkInfo != null && activeNetworkInfo.isConnected)
        }
    }

    private fun isNoInternet(context: Context) = !isNetConnected(context)

    private fun getDefaultCountry() = Country("MW", "Malawi", "+265", "KWACHA")

    private fun setDefaultCountry() {
        try {
            country = getDefaultCountry()
            val manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)?
            manager?.let {
                val countryCode = clearNull(manager.networkCountryIso)
                if (countryCode.isEmpty())
                    return
                val countries = Countries.getCountries()
                for (i in countries) {
                    if (i.code.equals(countryCode, true))
                        country = i
                }
                viewModel.setCountry(country!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun clearNull(str: String?) = str?.trim() ?: ""

    private fun subscribeObservers() {
        try {
            sharedViewModel.country.observe(viewLifecycleOwner) {
                viewModel.setCountry(it)
            }

            viewModel.getProgress().observe(viewLifecycleOwner) {
                progressView?.toggle(it)
            }

            viewModel.getVerificationId().observe(viewLifecycleOwner) { vCode ->
                vCode?.let {
                    viewModel.setProgress(false)
                    viewModel.resetTimer()
                    viewModel.setVCodeNull()
                    viewModel.setEmptyText()
                    if (findNavController().isValidDestination(R.id.loginFragment))
                        findNavController().navigate(R.id.action_loginFragment_to_verifyFragment)
                }
            }

            viewModel.getFailed().observe(viewLifecycleOwner) {
                progressView?.dismiss()
            }

            viewModel.getTaskResult().observe(viewLifecycleOwner) { taskId ->
                if (taskId != null && viewModel.getCredential().value?.smsCode.isNullOrEmpty())
                    //viewModel.fetchUser(taskId)
                    true
            }

            viewModel.userProfileGot.observe(viewLifecycleOwner) { success ->
                if (success && viewModel.getCredential().value?.smsCode.isNullOrEmpty()
                    && findNavController().isValidDestination(R.id.loginFragment)
                ) {
                    requireActivity().toastLong("Authenticated successfully using Instant verification")
                    findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /*       val action = FMobileDirections.actionFMobileToFVerify(
             Country(
                 code = "sd",
                 name = "sda",
                 noCode = "+83",
                 money = "mon"
             )
         )
         findNavController().navigate(action)*/

    override fun onDestroy() {
        try {
            progressView?.dismissIfShowing()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }
