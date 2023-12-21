package id.ac.ukdw.pointofsale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.database.dao.MenuDao
import javax.inject.Inject

@HiltViewModel
class PageMenuViewModel @Inject constructor(
    private val menuDao: MenuDao
) : ViewModel() {
    val menuItems: LiveData<List<MenuItem>> = menuDao.getMenuItems()

    // This function can be called from the fragment to fetch menu items
    fun getMenuItemsFromDB(): LiveData<List<MenuItem>> {
        // Perform any other operations here if needed
        // For example, if you want to clear the database before fetching:
        // menuDao.deleteAllMenuItems()

        // Retrieve menu items from the database and return the LiveData
        return menuDao.getMenuItems()
    }
}