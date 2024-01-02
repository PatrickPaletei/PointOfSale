package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class DataUser(
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("nama_karyawan")
    val namaKaryawan: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("image")
    val image: String?
)