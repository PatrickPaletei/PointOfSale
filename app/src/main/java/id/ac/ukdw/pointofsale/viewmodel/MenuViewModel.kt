package id.ac.ukdw.pointofsale.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.Service.ApiClientInterface
import id.ac.ukdw.pointofsale.api.Service.ApiService
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.database.dao.MenuDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuDao: MenuDao,
    private val apiClientInterface: ApiClientInterface
) : ViewModel() {

    val menuData: LiveData<List<MenuItem>> = menuDao.getMenuItems()

    fun fetchMenuData() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.getAllMenu().execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { apiResponse ->
                        Log.d("body", "fetchMenuData: $apiResponse")
                        val menuItemsFromAPI = mapApiResponseToMenuItem(apiResponse)

                        // Get existing data from the LiveData
                        val menuItemsFromDB = menuData.value ?: emptyList()

                        // Compare and update the database
                        updateMenuItems(menuItemsFromDB, menuItemsFromAPI)
                    }
                } else {
                    // Handle unsuccessful API call (e.g., show error message)
                }
            } catch (e: IOException) {
                // Log the exception
                Log.e("MenuViewModel", "Error fetching menu data: ${e.message}")
                // You may also consider using LiveData to communicate this error to your UI.
            }
        }
    }

    private suspend fun updateMenuItems(
        oldItems: List<MenuItem>,
        newItems: List<MenuItem>
    ) {
        val updatedItems = mutableListOf<MenuItem>()

        // Compare API data with the data from LiveData
        for (newItem in newItems) {
            val existingItem = oldItems.find { it.idMenu == newItem.idMenu }

            if (existingItem != null) {
                if (existingItem != newItem) {
                    updatedItems.add(newItem)
                } else {
                    updatedItems.add(existingItem)
                }
            } else {
                updatedItems.add(newItem)
            }
        }

        // Update the database with the updated items
        if (updatedItems.isNotEmpty()) {
            menuDao.deleteAllMenuItems()
            menuDao.insertMenuItems(updatedItems)
        }
    }

    fun mapApiResponseToMenuItem(apiResponse: AllMenuResponse): List<MenuItem> {
        return apiResponse.data.map { apiMenuItem ->
            MenuItem(
                idMenu = apiMenuItem.idMenu,
                namaMenu = apiMenuItem.namaMenu,
                harga = apiMenuItem.harga,
                image = apiMenuItem.image,
                jumlahStok = apiMenuItem.jumlahStok,
                kategori = apiMenuItem.kategori
            )
        }
    }
}














