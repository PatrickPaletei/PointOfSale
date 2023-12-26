package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class RegisterUserResponse(
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("nama_karyawan")
    val namaKaryawan: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("username")
    val username: String
)