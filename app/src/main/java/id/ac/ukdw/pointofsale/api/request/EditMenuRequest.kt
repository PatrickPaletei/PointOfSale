package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class EditMenuRequest(
    @SerializedName("harga")
    val harga: Int,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("nama_menu")
    val namaMenu: String
)