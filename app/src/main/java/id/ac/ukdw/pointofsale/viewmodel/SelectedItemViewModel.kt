package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ukdw.pointofsale.data.CardData

class SelectedItemViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<CardData>()
    val selectedItem: LiveData<CardData> get() = _selectedItem

    private val _checkOut = MutableLiveData<Boolean>()
    val checkOut:LiveData<Boolean> get() = _checkOut

    private val _cetakNota = MutableLiveData<Boolean>()
    val cetakNota:LiveData<Boolean> get() = _cetakNota

    fun setSelectedItem(item: CardData) {
        _selectedItem.value = item
    }

    //buat popup checkOut
    fun setcallPopUp(value:Boolean){
        _checkOut.value = value
    }

    fun setCallPopUpNota(value: Boolean){
        _cetakNota.value = value
    }
}
