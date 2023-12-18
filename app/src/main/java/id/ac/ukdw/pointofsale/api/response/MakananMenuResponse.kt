package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class MakananMenuResponse(
    @SerializedName("data")
    val `data`: List<DataMakanan>,
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int
)