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

    private val _tambahMenu = MutableLiveData<Boolean>()
    val tambahMenu: LiveData<Boolean> get() = _tambahMenu
    fun setCallPopUpTambahMenu(value: Boolean) {
        _tambahMenu.value = value
    }

    private val _editMenu = MutableLiveData<Boolean>()
    val editMenu: LiveData<Boolean> get() = _editMenu
    fun setCallPopUpEditMenu(value: Boolean) {
        _editMenu.value = value
    }

    private val _deleteMenu = MutableLiveData<Boolean>()
    val deleteMenu: LiveData<Boolean> get() = _deleteMenu
    fun setCallPopUpDeleteMenu(value: Boolean) {
        _deleteMenu.value = value
    }
}
