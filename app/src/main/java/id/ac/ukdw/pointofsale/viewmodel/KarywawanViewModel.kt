package id.ac.ukdw.pointofsale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.ukdw.pointofsale.api.Service.ApiClientInterface
import id.ac.ukdw.pointofsale.api.request.DeleteRequest
import id.ac.ukdw.pointofsale.api.request.RegisterRequest
import id.ac.ukdw.pointofsale.api.response.GetUserResponse
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
class KarywawanViewModel @Inject constructor(
    private val apiClientInterface: ApiClientInterface
) : ViewModel() {
    private val _responseUser = MutableLiveData<GetUserResponse?>()
    val responseUser: LiveData<GetUserResponse?> get() = _responseUser

    fun getAllUser() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.getUser().execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _responseUser.value = body
                        Log.d("all user", "getAllUser: $body")
                    } else {
                        Log.d("all user", "failed to get all user $body")
                    }
                }
            } catch (e: IOException) {
                Log.e("KaryawanViewModel", "Error get all user: ${e.message} ")
            }
        }
    }

    private val _responseCodeRegister = MutableLiveData<Int?>()
    val responseCodeRegister: LiveData<Int?> get() = _responseCodeRegister

    fun regisUser(file: File,
                  username: String,
                  nama_karyawan: String,
                  passwrod: String,
                  token: String) {
        viewModelScope.launch {
            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                val usernamePart = username.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val nama_karyawan_part = nama_karyawan.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val passwrodPart = passwrod.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                val respone = withContext(Dispatchers.IO) {
                    apiClientInterface.instance.registerUser(
                        token,
                        file = filePart,
                        username = usernamePart,
                        nama = nama_karyawan_part,
                        pass = passwrodPart
                    ).execute()
                }
                if (respone.isSuccessful) {
                    val body = respone.body()
                    val codeBOdy = body?.statusCode
                    if (codeBOdy == 200) {
                        _responseCodeRegister.value = codeBOdy
                        Log.d("regis user", "berhasil regis user $body")
                    } else {
                        Log.d("regis user", "fgagal regis user $codeBOdy")
                    }
                }
            } catch (e: IOException) {
                Log.e("KaryawanViewModel", "Error regis user: ${e.message} ")
            }
        }
    }

    private val _responseCodeDelete = MutableLiveData<Int?>()
    val responseCodeDelete: LiveData<Int?> get() = _responseCodeDelete

    fun deleteUser(id:Int){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    apiClientInterface.instance.deleteUser(id).execute()
                }
                if (response.isSuccessful){
                    val code = response.code()
                    val body = response.body()
                    if (code == 200){
                        _responseCodeDelete.value = code
                        Log.d("delete user", "berhasil delete user $body")
                    } else{
                        Log.d("delte user", "gagal delete user $body")
                    }
                }
            }catch (e: IOException) {
                Log.e("KaryawanViewModel", "Error delete user: ${e.message} ")
            }
        }
    }
}