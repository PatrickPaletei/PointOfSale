package id.ac.ukdw.pointofsale.api.request


import com.google.gson.annotations.SerializedName

data class DeleteRequest(
    @SerializedName("id_user")
    val idUser: Int
)