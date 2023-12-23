package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataSummary(
    @SerializedName("harga")
    val harga: Int,
    @SerializedName("jumlah")
    val jumlah: Int,
    @SerializedName("menu")
    val menu: String
)