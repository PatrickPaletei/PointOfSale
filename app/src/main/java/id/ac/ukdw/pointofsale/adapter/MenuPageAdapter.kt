package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.databinding.ItemMenuHalamanMenuBinding

class MenuPageAdapter(
    private val onItemClick: (String, String, String, Int) -> Unit,
    private val onItemDeleteClick: (Int,String) -> Unit,
) : RecyclerView.Adapter<MenuPageAdapter.ViewHolder>() {

    private var originalData: List<MenuItem> = listOf()
    private var currentData: List<MenuItem> = listOf()

    private val diffCallback = object : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.id == newItem.id // Assuming id is unique for items
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(items: List<MenuItem>) {
        originalData = items
        currentData = items // Update currentData when new list is submitted
        differ.submitList(items)
    }

    fun filterByCategory(category: String): Int {
        val filteredList = if (category.isNotEmpty()) {
            originalData.filter { it.kategori == category }
        } else {
            originalData // Return original unfiltered data if category is empty
        }
        differ.submitList(filteredList)

        // Return 1 if at least one item matches the category, otherwise return 0
        return if (filteredList.isNotEmpty()) 1 else 0
    }

    fun filterByInput(inputString: String): Int {
        val filteredList = if (inputString.isNotEmpty()) {
            originalData.filter { data ->
                data.namaMenu.contains(inputString, ignoreCase = true)
                // You can modify the condition based on your filter requirement
            }
        } else {
            emptyList() // Return an empty list if inputString is empty
        }
        differ.submitList(filteredList)
        return filteredList.size // Return the size of the filtered list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMenuHalamanMenuBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = differ.currentList[position]
        holder.bind(
            menuItem,
            onButton1Click = { item ->
                // Action when button 1 is clicked for this menuItem
                onItemClick(item.namaMenu, item.kategori, item.harga , item.idMenu)
            },
            onButton2Click = { item ->
                // Action when button 2 is clicked for this menuItem
                onItemDeleteClick(item.idMenu,item.namaMenu)
            }
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(private val binding: ItemMenuHalamanMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            menuItem: MenuItem,
            onButton1Click: (MenuItem) -> Unit,
            onButton2Click: (MenuItem) -> Unit
        ) {
            binding.apply {
                judulMenu.text = menuItem.namaMenu
                val formatHarga = menuItem.harga.removeSuffix(".00")
                val hargaWithIDR = "IDR $formatHarga"
                harga.text = hargaWithIDR
                kategori.text = menuItem.kategori

                // Set click listeners for buttons
                editMenu.setOnClickListener { onButton1Click(menuItem) }
                hapusMenu.setOnClickListener { onButton2Click(menuItem) }

                Glide.with(binding.root.context)
                    .load("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.facebook.com%2Fpostmalone%2F%3Flocale%3Did_ID&psig=AOvVaw0aMheJ-yxHfEWXOQC_o3RY&ust=1702412900037000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCPD9o8KdiIMDFQAAAAAdAAAAABAE" // Replace with your image URL or local path
                    )
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                    .placeholder(R.drawable.ayam) // Placeholder while loading
                    .error(R.drawable.ayam) // Error placeholder if image fails to load
                    .into(imgMakanan)
            }
        }
    }
}
