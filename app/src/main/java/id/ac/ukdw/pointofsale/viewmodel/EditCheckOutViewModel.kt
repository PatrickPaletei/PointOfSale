package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ukdw.pointofsale.data.CheckOutData

class EditCheckOutViewModel : ViewModel() {
    private val _checkOutData = MutableLiveData<CheckOutData>()
    val checkOutData: LiveData<CheckOutData> get() = _checkOutData

    private val _isPopupShown = MutableLiveData<Boolean>()
    val isPopupShown: LiveData<Boolean> get() = _isPopupShown

    init {
        _isPopupShown.value = false // Initially, the popup is not shown
    }

    // Function to set the initial CheckOutData
    fun setSelectedItem(item: CheckOutData) {
        _checkOutData.value = item
    }

    // Function to update the CheckOutData
    fun updateCheckOutData(updatedData: CheckOutData) {
        _checkOutData.value = updatedData
    }

    fun setPopUpFlag(){
        _isPopupShown.value = true
    }

    fun resetPopupFlag() {
        _isPopupShown.value = false // Reset the flag when needed
    }
}
