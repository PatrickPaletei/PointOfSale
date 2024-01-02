package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("plain_text_token")
    val plainTextToken: String,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("id_user")
    val id_user: Int,
    @SerializedName("image")
    val image: String?,
)