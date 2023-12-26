package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class GetUserResponse(
    @SerializedName("data")
    val `data`: List<DataUser>,
    @SerializedName("statusCode")
    val statusCode: Int
)