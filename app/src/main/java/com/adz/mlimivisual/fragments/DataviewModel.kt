package com.adz.mlimivisual.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataViewModel : ViewModel(){

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    fun setDescription(descrip: String) {
        _description.value = descrip
    }
    fun setLocation(locale: String) {
        _location.value = locale
    }
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }
    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }


    // Possible date options
    val dateOptions: List<String> = getPickupOptions()

    // Pickup date
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    fun setDate(pickupDate: String) {
        _date.value = pickupDate
    }
    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(7) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }
}