package id.ac.ukdw.pointofsale.data

data class CheckOutData(
    val image:String,
    val id_menu:Int,
    val judulMenu: String,
    val harga: String,
    var jumlah_pesanan: Int,
    var catatan: String
)
