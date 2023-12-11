package id.ac.ukdw.pointofsale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.adapter.SidebarAdapter
import id.ac.ukdw.pointofsale.data.SidebarItem
import id.ac.ukdw.pointofsale.ui.DashboardFragment
import id.ac.ukdw.pointofsale.ui.MenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }

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
            Toast.makeText(this, "Clicked on item $position", Toast.LENGTH_SHORT).show()
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
}