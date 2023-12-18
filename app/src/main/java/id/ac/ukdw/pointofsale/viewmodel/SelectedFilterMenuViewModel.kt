package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedFilterMenuViewModel: ViewModel() {

    private val dataFilter = MutableLiveData<Int>()
    val dataList: LiveData<Int> get() = dataFilter

    init {
        dataFilter.value = 1
    }

    fun updateData(newDataList: Int) {
        dataFilter.value = newDataList
    }

    fun getDataList(): Int? {
        return dataFilter.value
    }

}