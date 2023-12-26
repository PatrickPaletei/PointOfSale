package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("user")
    val user: RegisterUserResponse
)