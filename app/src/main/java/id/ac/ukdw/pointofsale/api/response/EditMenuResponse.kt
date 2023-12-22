package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class EditMenuResponse(
    @SerializedName("data")
    val `data`: DataEditMenu,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)