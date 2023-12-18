package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.databinding.ItemMenuDashboardBinding


class CardAdapterAllMenu(
    private val onItemClick: OnClickListener
) : RecyclerView.Adapter<CardAdapterAllMenu.CardViewHolder>() {

    private var originalData: List<DataSemuaMakanan> = listOf()
    private var filteredData: List<DataSemuaMakanan> = listOf()

    private val diffCallback = object : DiffUtil.ItemCallback<DataSemuaMakanan>() {
        override fun areItemsTheSame(
            oldItem: DataSemuaMakanan, newItem: DataSemuaMakanan
        ): Boolean {
            return oldItem.idMenu == oldItem.idMenu
        }

        override fun areContentsTheSame(
            oldItem: DataSemuaMakanan,
            newItem: DataSemuaMakanan
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(value: List<DataSemuaMakanan>) {
        originalData = value // Save original unfiltered data
        differ.submitList(value)
    }

    fun filterByCategory(category: String) {
        val filteredList = if (category.isNotEmpty()) {
            originalData.filter { it.kategori == category }
        } else {
            originalData // Return original unfiltered data if category is empty
        }
        differ.submitList(filteredList)
    }

    inner class CardViewHolder(private val binding: ItemMenuDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Access your views using binding instead of findViewById
        fun bind(dataSemuaMakanan: DataSemuaMakanan) {
            binding.apply {
                namaMenu.text = dataSemuaMakanan.namaMenu
                val formattedHarga = dataSemuaMakanan.harga.removeSuffix(".00")
                val hargaWithIDR = "IDR $formattedHarga"
                harga.text = hargaWithIDR
                root.setOnClickListener {
                    onItemClick.onClickItem(dataSemuaMakanan)
                }
            }
        }
    }


    // Function to filter data by category (kategori)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding =
            ItemMenuDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface OnClickListener {
        fun onClickItem(dataSemuaMakanan: DataSemuaMakanan)
    }
}