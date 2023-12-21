package id.ac.ukdw.pointofsale.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MenuViewModel : ViewModel() {
    private val _menuData = MutableLiveData<List<DataSemuaMakanan>>()
    val menuData: LiveData<List<DataSemuaMakanan>> get() = _menuData

    fun fetchMenuData() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.instance.getAllMenu().execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        _menuData.value = it.data
                    }
                } else {
                    // Handle unsuccessful API call (e.g., show error message)
                }
            } catch (e: IOException) {
                // Handle network error
            }
        }
    }
}









