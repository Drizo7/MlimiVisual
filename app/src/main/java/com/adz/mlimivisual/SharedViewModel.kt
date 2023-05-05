package com.adz.mlimivisual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adz.mlimivisual.fragments.login.LoginRepo
import com.adz.mlimivisual.models.Country
import com.adz.mlimivisual.utils.ScreenState
import com.adz.mlimivisual.utils.printMeD
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


class SharedViewModel : ViewModel() {

    val country = MutableLiveData<Country>()

    val openMainAct = MutableLiveData<Boolean>()

    private val _state = MutableLiveData<ScreenState>(ScreenState.IdleState)

    val lastQuery = MutableLiveData<String>()

    private val listOfQuery = arrayListOf("")

    private var timer: TimerTask? = null

    private val repo = LoginRepo.getInstance()

    init {
        "Init SharedViewModel".printMeD()
    }

    fun getLastQuery(): LiveData<String> {
        return lastQuery
    }

    fun setLastQuery(query: String) {
        Timber.v("Last Query $query")
        listOfQuery.add(query)
        lastQuery.value = query
    }

    fun setState(state: ScreenState) {
        Timber.v("State $state")
        _state.value = state
    }

    fun getState(): LiveData<ScreenState> {
        return _state
    }

    fun setCountry(country: Country) {
        this.country.value = country
    }
    fun currentUser(){
         repo.getCurrentUserId()
    }
    fun currentUserExist():Boolean {
        if (repo.currentUserExist()){
            return true
        }
        return false
    }
    fun signOut(){
        repo.signOut()
    }

    fun onFromSplash() {
        if (timer == null) {
            timer = Timer().schedule(2000) {
                openMainAct.postValue(true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        "onCleared SharedViewModel".printMeD()
    }

}

annotation class AndroidViewModel
