package id.ac.ukdw.pointofsale.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.ac.ukdw.pointofsale.database.dao.MenuDao

@Database(entities = [MenuItem::class], version = 1, exportSchema = false)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
}
