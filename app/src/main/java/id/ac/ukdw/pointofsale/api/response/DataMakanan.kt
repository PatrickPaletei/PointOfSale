package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataMakanan(
    @SerializedName("harga")
    val harga: String,
    @SerializedName("id_menu")
    val idMenu: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("jumlah_stok")
    val jumlahStok: Int,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("nama_menu")
    val namaMenu: String
)