package id.ac.ukdw.pointofsale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.request.ItemTransaksi
import id.ac.ukdw.pointofsale.api.request.TambahTransaksiRequest
import id.ac.ukdw.pointofsale.api.response.TransaksiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotaViewModel : ViewModel() {
    private val _responseBody = MutableLiveData<Int?>()
    val responseBody: LiveData<Int?>
        get() = _responseBody

    // Function to update the response body LiveData
    fun updateResponseBody(response: Int?) {
        _responseBody.value = response
    }

    fun tambahTransaksi(
        id_user: Int,
        items: List<ItemTransaksi>,
        metode_pembayaran: String,
        nama_pelanggan: String
    ) {
        val request = TambahTransaksiRequest(
            idUser = id_user,
            items = items,
            metodePembayaran = metode_pembayaran,
            namaPelanggan = nama_pelanggan
        )

        ApiClient.instance.tambahTransaksi(request)
            .enqueue(object : Callback<TransaksiResponse> {
                override fun onResponse(
                    call: Call<TransaksiResponse>,
                    response: Response<TransaksiResponse>
                ) {
                    val code = response.code()
                    val body = response.body()
                    if (code == 201) {
                        Log.d("Transaksi", "onResponse: $response ")
                        // Update LiveData with the response code
                        updateResponseBody(body?.idTransaksi)
                    } else {
                        updateResponseBody(null)
                    }
                }

                override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                    Log.d("Transaksi", "onFailure: $t ")
                    // Handle failure by setting LiveData to null or an appropriate value
                    updateResponseBody(null)
                }
            })
    }
}
