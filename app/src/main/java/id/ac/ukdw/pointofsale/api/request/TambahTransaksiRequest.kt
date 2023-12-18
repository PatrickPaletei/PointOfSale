package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class TambahTransaksiRequest(
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("items")
    val items: List<ItemTransaksi>,
    @SerializedName("metode_pembayaran")
    val metodePembayaran: String,
    @SerializedName("nama_pelanggan")
    val namaPelanggan: String
)