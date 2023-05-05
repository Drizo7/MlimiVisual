package com.adz.mlimivisual.fragments.login

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.adz.mlimivisual.R
import com.adz.mlimivisual.SharedViewModel
import com.adz.mlimivisual.utils.*
import com.adz.mlimivisual.databinding.FragmentVerifyBinding
import com.adz.mlimivisual.views.CustomProgressView
import com.google.firebase.auth.PhoneAuthProvider


class VerifyFragment : Fragment() {


    lateinit var binding: FragmentVerifyBinding

    //private val viewModel by activityViewModels<LogInViewModel>()

    private lateinit var edtTexts: ArrayList<EditText>

    private var progressView: CustomProgressView?=null
    private val viewModel: LogInViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel

        progressView = CustomProgressView(requireContext())

        setDataInView()
        subscribeObservers()
    }
    private fun setDataInView() {
        try {
            edtTexts = ArrayList()
            edtTexts.add(binding.edtOne)
            edtTexts.add(binding.edtTwo)
            edtTexts.add(binding.edtThree)
            edtTexts.add(binding.edtFour)
            edtTexts.add(binding.edtFive)
            edtTexts.add(binding.edtSix)
            addListener()
            if (viewModel.resendTxt.value.isNullOrEmpty())
                viewModel.startTimer()
            binding.btnVerify.setOnClickListener {
                validateOtp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateOtp() {
        try {
            val otp = getOtpValue()
            when {
                otp.length < 6 -> snack(requireActivity(), "Enter valid otp")
                isNoInternet(requireContext()) -> {
                    snackNet(requireActivity())
                }
                else -> {
                    "VCode:: ${viewModel.verifyCode}".printMeD()
                    "OTP:: $otp".printMeD()
                    val credential = PhoneAuthProvider.getCredential(viewModel.verifyCode, otp)
                    viewModel.setCredential(credential)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("MissingPermission")
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

    private fun getOtpValue(): String {
        try {
            var otp = ""
            for (edtTxt in edtTexts)
                otp += edtTxt.trim()
            return otp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun addListener() {
        try {
            for (editText in edtTexts) {
                editText.addTextChangedListener(OtpWatcher(editText))
                editText.setOnKeyListener { _, keyCode: Int, _ ->
                    if (keyCode == KeyEvent.KEYCODE_DEL)
                        onKeyListener()
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onKeyListener() {
        try {
            val edtView: EditText = edtTexts[viewModel.ediPosition]
            if (edtView.trim().isEmpty() && viewModel.ediPosition > 0) {
                viewModel.ediPosition -= 1
                edtTexts[viewModel.ediPosition].requestFocus()
            } else edtView.requestFocus()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            viewModel.getCredential().observe(viewLifecycleOwner) { credential ->
                credential?.let {
                    val otp = credential.smsCode
                    edtTexts.forEachIndexed { i, editText ->
                        editText.text = otp?.get(i)?.toEditable()
                    }
                    viewModel.setVProgress(true)
                }
            }

            viewModel.getVProgress().observe(viewLifecycleOwner) { show ->
                progressView?.toggle(show)
            }

            viewModel.getFailed().observe(viewLifecycleOwner) {
                viewModel.setVProgress(false)
            }

            viewModel.getVerificationId().observe(viewLifecycleOwner) { vCode ->
                vCode?.let {
                    viewModel.setVProgress(false)
                    viewModel.setVCodeNull()
                    viewModel.startTimer()
                }
            }

            viewModel.getTaskResult().observe(viewLifecycleOwner) { taskId ->
                taskId?.let {
                    //viewModel.fetchUser(taskId)
                }
            }

            viewModel.userProfileGot.observe(viewLifecycleOwner) { success ->
                if (success && findNavController().isValidDestination(R.id.profileFragment))
                    findNavController().navigate(R.id.action_verifyFragment_to_getUserDataFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class OtpWatcher(private val v: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (v.id) {
                R.id.edt_one ->
                    changeFocus(text, 0, 1)
                R.id.edt_two ->
                    changeFocus(text, 0, 2)
                R.id.edt_three ->
                    changeFocus(text, 1, 3)
                R.id.edt_four ->
                    changeFocus(text, 2, 4)
                R.id.edt_five ->
                    changeFocus(text, 3, 5)
                R.id.edt_six ->
                    changeFocus(text, 4, 5)
                else -> {
                    if (text.isEmpty())
                        edtTexts[5].requestFocus()
                }
            }
        }
    }

    private fun changeFocus(text: String, previous1: Int, next: Int) {
        viewModel.ediPosition = next - 1
        edtTexts[if (text.isEmpty()) previous1 else next].requestFocus()
    }

    override fun onDestroy() {
        progressView?.dismissIfShowing()
        viewModel.clearAll()
        super.onDestroy()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerifyFragment().apply {

            }
    }
}
