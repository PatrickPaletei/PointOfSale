package id.ac.ukdw.pointofsale.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ac.ukdw.pointofsale.database.MenuItem

@Dao
interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuItem>) // Example insert method

    @Query("SELECT * FROM menu_items")
    fun getMenuItems(): LiveData<List<MenuItem>> // Example query method

    @Query("DELETE FROM menu_items")
    suspend fun deleteAllMenuItems()
}

