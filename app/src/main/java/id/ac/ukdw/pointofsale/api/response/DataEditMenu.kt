package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataEditMenu(
    @SerializedName("harga")
    val harga: Int,
    @SerializedName("id_menu")
    val idMenu: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("jumlah_stok")
    val jumlahStok: Any,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("nama_menu")
    val namaMenu: String
)