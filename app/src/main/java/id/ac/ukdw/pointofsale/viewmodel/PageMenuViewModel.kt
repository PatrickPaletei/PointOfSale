package id.ac.ukdw.pointofsale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.ukdw.pointofsale.api.Service.ApiClientInterface
import id.ac.ukdw.pointofsale.api.request.EditMenuRequest
import id.ac.ukdw.pointofsale.api.request.TambahMenuRequest
import id.ac.ukdw.pointofsale.data.DataEditHelper
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.database.dao.MenuDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PageMenuViewModel @Inject constructor(
    private val menuDao: MenuDao,
    private val apiClientInterface: ApiClientInterface
) : ViewModel() {
    val menuItems: LiveData<List<MenuItem>> = menuDao.getMenuItems()

    // This function can be called from the fragment to fetch menu items
    fun getMenuItemsFromDB(): LiveData<List<MenuItem>> {
        // Retrieve menu items from the database and return the LiveData
        return menuDao.getMenuItems()
    }

    private val _menuAdded = MutableLiveData<Boolean>()
    val menuAdded: LiveData<Boolean> = _menuAdded

    fun addData(
        namaMenu: String,
        harga: Int,
        kategori: String,
        token: String,
        file: File,
    ) {
        viewModelScope.launch {
            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )

                val namaMenuPart = namaMenu.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val hargaPart = harga.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val kategoriPart = kategori.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.tambahMenu(
                        token = token,
                        file = filePart,
                        namaMenu = namaMenuPart,
                        harga = hargaPart,
                        kategori = kategoriPart
                        ).execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { apiResponse ->
                        Log.d("status tambah suskes", "addData: $apiResponse")
                    }
                    _menuAdded.value = true
                } else {
                    Log.d("status tambah gagal", "status tambah gagal")
                }
            } catch (e: IOException) {
                Log.e("pageMenuViewModel", "Error tambah menu: ${e.message} ")
            }
        }
    }

    private val _menuUpdated = MutableLiveData<Boolean>()
    val menuUpdated: LiveData<Boolean> = _menuUpdated

    fun editData(
        file: File,
        namaMenu: String,
        harga: Int,
        kategori: String,
        token: String,
        idMenu: Int
    ) {
        viewModelScope.launch {
            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )

                val namaMenuPart = namaMenu.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val hargaPart = harga.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val kategoriPart = kategori.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.editMenu(
                        token = token,
                        id = idMenu,
                        file = filePart,
                        namaMenu = namaMenuPart,
                        harga = hargaPart,
                        kategori = kategoriPart
                    ).execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { apiResponse ->
                        Log.d("status edit sukses", "editData: $apiResponse")
                    }
                    _menuUpdated.value = true
                } else {
                    Log.d("status edit gagal", "status edit gagal")
                }
            } catch (e: IOException) {
                Log.e("pageMenuViewModel", "Error editing menu: ${e.message} ")
            }
        }
    }

    private val _menuDeleted = MutableLiveData<Boolean>()
    val menuDeleted: LiveData<Boolean> = _menuDeleted
    fun deleteMenu(
        idMenu:Int,
        token: String
    ){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    apiClientInterface.instance.deleteMenu(token = token, id = idMenu).execute()
                }
                if (response.isSuccessful){
                    val body = response.body()
                    body?.let { apiResponse ->
                        Log.d("status delete suskes", "addData: $apiResponse")
                    }
                    _menuDeleted.value = true
                }
            }catch (e: IOException) {
                Log.e("pageMenuViewModel", "Error delete menu: ${e.message} ")
            }
        }
    }


}