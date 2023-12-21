package id.ac.ukdw.pointofsale.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // Example primary key, adjust as needed
    val idMenu: Int,
    val namaMenu: String,
    val harga: String,
    val image: String?,
    val jumlahStok: Int,
    val kategori: String
)
