package id.ac.ukdw.pointofsale

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import id.ac.ukdw.pointofsale.ui.PopUpFragment
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel

class MainActivity : AppCompatActivity(){
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigationRailView: NavigationRailView = findViewById(R.id.navigation_rail)

        // Set up NavController from NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navContainer) as NavHostFragment
        navController = navHostFragment.navController


        selectedItemViewModel = ViewModelProvider(this).get(SelectedItemViewModel::class.java)
        selectedItemViewModel.selectedItem.observe(this) { selectedItem ->
            // Handle changes to the selected item here
            showPopUpDialog()
        }

        // Set an item selected listener
        navigationRailView.setOnItemSelectedListener { item ->
            // Perform actions based on the selected item
            when (item.itemId) {
                R.id.dashboard -> {
                    navController.navigate(R.id.dashboardFragment2)
                }
                R.id.menu -> {
                    navController.navigate(R.id.editMenuFragment2)
                }
                R.id.penjualan -> {
                    navController.navigate(R.id.penjualanFragment)
                }
                // Add more cases for other menu items as needed
            }
            true // Return true to indicate item selection handled
        }


    }

    fun getSelectedItemViewModel(): SelectedItemViewModel {
        return selectedItemViewModel
    }

    fun showPopUpDialog() {
        val dialogFragment = PopUpFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }


}


