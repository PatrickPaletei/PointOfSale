package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataTambahMenu(
    @SerializedName("harga")
    val harga: String,
    @SerializedName("id_menu")
    val idMenu: Int,
    @SerializedName("image")
    val image: Any,
    @SerializedName("jumlah_stok")
    val jumlahStok: Any,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("nama_menu")
    val namaMenu: String
)