package id.ac.ukdw.pointofsale.api.Service

import id.ac.ukdw.pointofsale.api.request.LoginRequest
import id.ac.ukdw.pointofsale.api.request.TambahTransaksiRequest
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.LoginResponse
import id.ac.ukdw.pointofsale.api.response.TransaksiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    //login
    @POST("api/login")
    fun login(@Body request:LoginRequest):Call<LoginResponse>

    @GET("api/menu")
    fun getAllMenu():Call<AllMenuResponse>

    @POST("api/tambah-detail-transaksi")
    fun tambahTransaksi(@Body request: TambahTransaksiRequest):Call<TransaksiResponse>

}