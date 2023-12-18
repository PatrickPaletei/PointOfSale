package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class ItemTransaksi(
    @SerializedName("id_menu")
    val idMenu: Int,
    @SerializedName("jumlah_pesanan")
    val jumlahPesanan: Int,
    @SerializedName("catatan")
    val catatan: String
)