package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ukdw.pointofsale.data.CardData

class SelectedItemViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<CardData>()
    val selectedItem: LiveData<CardData> get() = _selectedItem

    fun setSelectedItem(item: CardData) {
        _selectedItem.value = item
    }
}
