package id.ac.ukdw.pointofsale.api.response


import com.google.gson.annotations.SerializedName

data class SummaryResponse(
    @SerializedName("data")
    val `data`: List<DataSummary>,
    @SerializedName("jumlah_pesanan")
    val jumlahPesanan: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("pendapatan")
    val pendapatan: Int,
    @SerializedName("statusCode")
    val statusCode: Int
)