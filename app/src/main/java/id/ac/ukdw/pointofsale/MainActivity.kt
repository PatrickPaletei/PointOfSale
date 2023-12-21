package id.ac.ukdw.pointofsale

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.ac.ukdw.pointofsale.adapter.SidebarAdapter
import id.ac.ukdw.pointofsale.data.SidebarItem
import id.ac.ukdw.pointofsale.ui.dashboard.CetakNotaFragment
import id.ac.ukdw.pointofsale.ui.dashboard.EditCheckOutFragment
import id.ac.ukdw.pointofsale.ui.dashboard.PopUpFragment
import id.ac.ukdw.pointofsale.ui.dashboard.PopUpPembayaranFragment
import id.ac.ukdw.pointofsale.viewmodel.EditCheckOutViewModel
import id.ac.ukdw.pointofsale.viewmodel.SelectedItemViewModel
@AndroidEntryPoint
class MainActivity : AppCompatActivity(){
    private lateinit var selectedItemViewModel: SelectedItemViewModel
    private lateinit var navController: NavController
    private lateinit var editCheckOutViewModel: EditCheckOutViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Set up NavController from NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navContainer) as NavHostFragment
        navController = navHostFragment.navController


        selectedItemViewModel = ViewModelProvider(this).get(SelectedItemViewModel::class.java)
        selectedItemViewModel.selectedItem.observe(this) { selectedItem ->
            // Handle changes to the selected item here
            showPopUpDialogMenu()
        }
        selectedItemViewModel.checkOut.observe(this){
            showPopUpDialogCheckOut()
        }
        // cannot memory not enough
        selectedItemViewModel.cetakNota.observe(this){
            showPopUpPrint()
//            selectedItemViewModel.setCallPopUpNota(false)
        }

        editCheckOutViewModel = ViewModelProvider(this).get(EditCheckOutViewModel::class.java)
        editCheckOutViewModel.isPopupShown.observe(this) { isShown ->
            if (isShown) {
                showPopUpDialogEdit()
                editCheckOutViewModel.resetPopupFlag() // Reset the flag after showing the popup
            }
        }
        sideBar()

    }

    fun getSelectedItemViewModel(): SelectedItemViewModel {
        return selectedItemViewModel
    }
    fun getCheckOutItemViewModel(): EditCheckOutViewModel {
        return editCheckOutViewModel
    }

    fun showPopUpPrint(){
        val dialogFragment = CetakNotaFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }

    fun showPopUpDialogEdit(){
        val dialogFragment = EditCheckOutFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }

    fun showPopUpDialogCheckOut(){
        val dialogFragment = PopUpPembayaranFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }

    fun showPopUpDialogMenu() {
        val dialogFragment = PopUpFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }

    fun showPopUpLogOut() {
        val dialogFragment = DialogLogoutFragment()
        dialogFragment.show(supportFragmentManager, "PopUp")
    }

    private fun sideBar(){
        val sidebarItems = listOf(
            SidebarItem(R.drawable.ic_dashboard_unselected,R.drawable.ic_dashboard, "Dashboard"),
            SidebarItem(R.drawable.ic_menu_unselected,R.drawable.ic_menu, "Menu"),
            SidebarItem(R.drawable.ic_penjualan_unselected,R.drawable.ic_penjualan, "Penjualan"),
            // Add more items as needed
        )

        val recyclerView: RecyclerView = findViewById(R.id.sidebarRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SidebarAdapter(sidebarItems) { position ->
            // Handle item click (e.g., highlight, change fragment, etc.)
            when(position){
                0 -> navController.navigate(R.id.dashboardFragment2)
                1 -> navController.navigate(R.id.editMenuFragment2)
                2 -> navController.navigate(R.id.penjualanFragment)
            }

        }

        val logOut:ImageButton = findViewById(R.id.logoutSide)
        logOut.setOnClickListener {
            showPopUpLogOut()
        }

        val profileAvatar: ImageView = findViewById(R.id.profileAvatar)
        val imageUrl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.facebook.com%2Fpostmalone%2F%3Flocale%3Did_ID&psig=AOvVaw0aMheJ-yxHfEWXOQC_o3RY&ust=1702412900037000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCPD9o8KdiIMDFQAAAAAdAAAAABAE" // Replace with your image URL or local path

        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .placeholder(R.drawable.man) // Placeholder image while loading
            .error(R.drawable.man) // Error image if loading fails
            .into(profileAvatar)
    }

    override fun onDestroy() {
        // Clearing the "isLoggedIn" flag when the activity is being destroyed
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false) // Set to false when the app is closing
        editor.apply()

        super.onDestroy()
    }
}


