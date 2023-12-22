package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class TambahMenuResponse(
    @SerializedName("data")
    val `data`: DataTambahMenu,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)