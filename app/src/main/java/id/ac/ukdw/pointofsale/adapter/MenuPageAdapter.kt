package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.database.MenuItem
import id.ac.ukdw.pointofsale.databinding.ItemMenuHalamanMenuBinding

class MenuPageAdapter(
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuPageAdapter.ViewHolder>() {

    private var originalData: List<MenuItem> = listOf()

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
        differ.submitList(items)
    }
    fun filterByCategory(category:String){
        val filteredList = if (category.isNotEmpty()) {
            originalData.filter { it.kategori == category }
        } else {
            originalData // Return original unfiltered data if category is empty
        }
        differ.submitList(filteredList)
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
        holder.bind(menuItem)
        holder.itemView.setOnClickListener { onItemClick(menuItem) }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(private val binding: ItemMenuHalamanMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.apply {
                judulMenu.text = menuItem.namaMenu
                val formatHarga = menuItem.harga.removeSuffix(".00")
                val hargaWithIDR = "IDR $formatHarga"
                harga.text = hargaWithIDR
                kategori.text = menuItem.kategori
                // Bind other data as needed
            }
        }
    }
}
