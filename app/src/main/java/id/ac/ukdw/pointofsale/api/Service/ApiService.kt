package id.ac.ukdw.pointofsale.api.Service

import id.ac.ukdw.pointofsale.api.request.EditMenuRequest
import id.ac.ukdw.pointofsale.api.request.LoginRequest
import id.ac.ukdw.pointofsale.api.request.TambahMenuRequest
import id.ac.ukdw.pointofsale.api.request.TambahTransaksiRequest
import id.ac.ukdw.pointofsale.api.response.AllMenuResponse
import id.ac.ukdw.pointofsale.api.response.DeleteResponse
import id.ac.ukdw.pointofsale.api.response.EditMenuResponse
import id.ac.ukdw.pointofsale.api.response.LoginResponse
import id.ac.ukdw.pointofsale.api.response.SummaryResponse
import id.ac.ukdw.pointofsale.api.response.TambahMenuResponse
import id.ac.ukdw.pointofsale.api.response.TransaksiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //login
    @POST("api/login")
    fun login(@Body request:LoginRequest):Call<LoginResponse>

    // menu
    @GET("api/menu")
    fun getAllMenu():Call<AllMenuResponse>

    // post transaksi
    @POST("api/tambah-detail-transaksi")
    fun tambahTransaksi(@Body request: TambahTransaksiRequest):Call<TransaksiResponse>

    // get nota
    @Headers(
        "Accept:application/pdf",
        "Content-Type:application/pdf"
    )
    @GET("api/nota-transaksi")
    fun cetakNota(@Header("idtransaksi") idtransaksi:Int):Call<ResponseBody>

    // tambah menu
    @POST("api/tambah-menu")
    fun tambahMenu(@Header("Authorization") token:String,@Body request:TambahMenuRequest):Call<TambahMenuResponse>


    //edit menu
    @PUT("api/edit-menu/{id}")
    fun editMenu(@Header("Authorization") token:String,@Path("id") id: Int,@Body request:EditMenuRequest) : Call<EditMenuResponse>

    @DELETE("api/hapus-menu/{id}")
    fun deleteMenu(@Header("Authorization")token: String,@Path("id") id:Int) : Call<DeleteResponse>

    //exportexcel
    @GET("api/export")
    fun export(@Header("Authorization")token: String):Call<ResponseBody>

    //summary
    @GET("api/summary-penjualan")
    fun getSummaryToday(@Header("criteria")criteria:String):Call<SummaryResponse>
}