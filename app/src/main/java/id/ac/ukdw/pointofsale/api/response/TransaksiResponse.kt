package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class TransaksiResponse(
    @SerializedName("data")
    val `data`: DataIdTransaksi,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)