package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class TambahMenuRequest(
    @SerializedName("harga")
    val price: Int,
    @SerializedName("kategori")
    val category: String,
    @SerializedName("nama_menu")
    val namaMenu: String
)