package id.ac.ukdw.pointofsale.api.Service

import id.ac.ukdw.pointofsale.api.request.LoginRequest
import id.ac.ukdw.pointofsale.api.request.RegisterRequest
import id.ac.ukdw.pointofsale.api.request.TambahTransaksiRequest
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.DeleteResponse
import id.ac.ukdw.pointofsale.api.response.EditMenuResponse
import id.ac.ukdw.pointofsale.api.response.GetUserResponse
import id.ac.ukdw.pointofsale.api.response.LoginResponse
import id.ac.ukdw.pointofsale.api.response.RegisterResponse
import id.ac.ukdw.pointofsale.api.response.SummaryResponse
import id.ac.ukdw.pointofsale.api.response.TambahMenuResponse
import id.ac.ukdw.pointofsale.api.response.TransaksiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    //login
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // menu
    @GET("api/menu")
    fun getAllMenu(): Call<AllMenuResponse>

    // post transaksi
    @POST("api/tambah-detail-transaksi")
    fun tambahTransaksi(@Body request: TambahTransaksiRequest): Call<TransaksiResponse>

    // get nota
    @Headers(
        "Accept:application/pdf",
        "Content-Type:application/pdf"
    )
    @GET("api/nota-transaksi")
    fun cetakNota(@Header("idtransaksi") idtransaksi: Int): Call<ResponseBody>

    // tambah menu
    @Multipart
    @POST("api/tambah-menu")
    fun tambahMenu(
        @Header("Authorization") token: String,
        @Part("nama_menu") namaMenu: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<TambahMenuResponse>


    //edit menu
    @Multipart
    @POST("api/edit-menu/{id}")
    fun editMenu(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part file: MultipartBody.Part,
        @Part("nama_menu") namaMenu: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("kategori") kategori: RequestBody
    ): Call<EditMenuResponse>

    //regis user
    @Multipart
    @POST("api/register")
    fun registerUser(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part ("username") username:RequestBody,
        @Part ("nama_karyawan") nama:RequestBody,
        @Part ("password") pass:RequestBody
    ): Call<RegisterResponse>

    @DELETE("api/hapus-menu/{id}")
    fun deleteMenu(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<DeleteResponse>

    //exportexcel
    @GET("api/export")
    fun export(@Header("Authorization") token: String): Call<ResponseBody>

    //summary
    @GET("api/summary-penjualan")
    fun getSummaryToday(@Header("criteria") criteria: String): Call<SummaryResponse>

    @GET("api/user")
    fun getUser(): Call<GetUserResponse>



    @DELETE("api/destroy/{id}")
    fun deleteUser(@Path("id") id: Int): Call<DeleteResponse>
}