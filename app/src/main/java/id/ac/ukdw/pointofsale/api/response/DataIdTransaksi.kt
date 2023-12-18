package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataIdTransaksi(
    @SerializedName("id_transaksi")
    val idTransaksi: Int
)