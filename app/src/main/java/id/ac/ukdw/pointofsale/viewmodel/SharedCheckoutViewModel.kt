package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ukdw.pointofsale.data.CheckOutData

class SharedCheckoutViewModel : ViewModel() {
    private val _dataList = MutableLiveData<List<CheckOutData>>()
    val dataList: LiveData<List<CheckOutData>> get() = _dataList

    // Initialize the LiveData with an empty list
    init {
        _dataList.value = emptyList()
    }

    // Function to update the list of CheckOutData
    fun updateData(newDataList: List<CheckOutData>) {
        _dataList.value = newDataList
    }

    // Function to retrieve the current list of CheckOutData
    fun getDataList(): List<CheckOutData>? {
        return _dataList.value
    }

    fun clearData() {
        _dataList.value = emptyList()
    }

    fun removeIfEmpty() {
        val currentList = _dataList.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.jumlah == 0 } // Remove items with quantity 0
        _dataList.value = currentList
    }

}