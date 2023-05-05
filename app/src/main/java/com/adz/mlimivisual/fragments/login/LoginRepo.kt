package com.adz.mlimivisual.fragments.login

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adz.mlimivisual.MainActivity
import com.adz.mlimivisual.models.Country
import com.adz.mlimivisual.utils.printMeD
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LoginRepo : PhoneAuthProvider.OnVerificationStateChangedCallbacks()
{
    private val verificationId: MutableLiveData<String> = MutableLiveData()

    private val credential: MutableLiveData<PhoneAuthCredential> = MutableLiveData()

    private val taskResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()

    private val failedState: MutableLiveData<LogInFailedState> = MutableLiveData()

    private val auth = FirebaseAuth.getInstance()

    private val actContxt = MainActivity.getInstance()

    companion object {
        fun getInstance(): LoginRepo = LoginRepo()
    }

    fun setMobile(country: Country, mobile: String) {
        Timber.v("Mobile $mobile")
        val number = country.noCode + " " + mobile
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(actContxt)
            .setCallbacks(this)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /*private fun requireActivity(): Activity {
         return MainActivity()
    }*/


    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        Timber.v("onVerificationCompleted:$credential")
        this.credential.value = credential
        Handler(Looper.getMainLooper()).postDelayed({
            signInWithPhoneAuthCredential(credential)
        }, 1000)
    }

    override fun onVerificationFailed(exp: FirebaseException) {
        "onVerficationFailed:: ${exp.message}".printMeD()
        failedState.value = LogInFailedState.Verification
        when (exp) {
            is FirebaseAuthInvalidCredentialsException ->
                Timber.v("Invalid Request")
            else -> Timber.v(exp.message.toString())
        }
    }

    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        Timber.v("onCodeSent:$verificationId")
        this.verificationId.value = verificationId
        Timber.v("Verification code sent successfully")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.v("signInWithCredential:success")
                    taskResult.value = task
                } else {
                    Timber.v("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                        Timber.v("Invalid verification code!")
                    failedState.value = LogInFailedState.SignIn
                }
            }
    }

    fun setCredential(credential: PhoneAuthCredential) {
        signInWithPhoneAuthCredential(credential)
    }

    fun getVCode(): MutableLiveData<String> {
        return verificationId
    }

    fun setVCodeNull() {
        verificationId.value = null
    }

    fun clearOldAuth(){
        credential.value=null
        taskResult.value=null
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return credential
    }

    fun getTaskResult(): LiveData<Task<AuthResult>> {
        return taskResult
    }

    fun currentUserExist():Boolean {
        if (auth.currentUser != null){
            return true
        }
        return false
    }

    fun signOut(){
        auth.signOut()
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid!!
    }

    fun getFailed(): LiveData<LogInFailedState> {
        return failedState
    }

    enum class LogInFailedState {
        Verification,
        SignIn
    }
}