package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nama_karyawan")
    val namaKaryawan: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String
)