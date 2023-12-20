package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedFilterMenuViewModel: ViewModel() {

    private val dataFilter = MutableLiveData<Int>()
    val dataList: LiveData<Int> get() = dataFilter

    private val stringFilter = MutableLiveData<String?>()
    val dataStringFilter: LiveData<String?> get() = stringFilter

    private val combinedFilters = MediatorLiveData<Pair<Int?, String?>>()
    val combinedFilter: LiveData<Pair<Int?, String?>> get() = combinedFilters

    private val stateUi = MutableLiveData<Boolean>()
    val dataState:LiveData<Boolean> get() = stateUi

    init {
        dataFilter.value = 1
        stringFilter.value = null
        stateUi.value = false

        combinedFilters.addSource(dataFilter) {
            combinedFilters.value = Pair(dataFilter.value, stringFilter.value)
        }

        combinedFilters.addSource(stringFilter) {
            combinedFilters.value = Pair(dataFilter.value, stringFilter.value)
        }
    }

    fun updateUi(state:Boolean){
        stateUi.value = state
    }

    fun updateData(newDataList: Int, query: String?) {
        dataFilter.value = newDataList
        stringFilter.value = query
    }

    fun getDataList(): Int? {
        return dataFilter.value
    }

}