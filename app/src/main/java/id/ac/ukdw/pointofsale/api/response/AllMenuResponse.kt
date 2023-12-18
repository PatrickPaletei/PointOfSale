package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class AllMenuResponse(
    @SerializedName("data")
    val `data`: List<DataSemuaMakanan>,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)