package id.ac.ukdw.pointofsale.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.databinding.ItemMenuDashboardBinding
import id.ac.ukdw.pointofsale.viewmodel.SharedCheckoutViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class CardAdapterAllMenu(
    private val onItemClick: OnClickListener,
    private val checkoutViewModel: SharedCheckoutViewModel
) : RecyclerView.Adapter<CardAdapterAllMenu.CardViewHolder>() {

    private var originalData: List<DataSemuaMakanan> = listOf()
    private var filteredData: List<DataSemuaMakanan> = listOf()
    private val selectedItems: MutableList<DataSemuaMakanan> = mutableListOf()


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

    fun filterByCategory(category: String): Int {
        val filteredList = if (category.isNotEmpty()) {
            originalData.filter { it.kategori == category }
        } else {
            originalData // Return original unfiltered data if category is empty
        }
        differ.submitList(filteredList)
        return filteredList.size
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


    inner class CardViewHolder(private val binding: ItemMenuDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataSemuaMakanan: DataSemuaMakanan, isSelected: Boolean) {
            binding.apply {

                Glide.with(binding.root.context)
                    .load(dataSemuaMakanan.image)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                    .placeholder(R.drawable.ic_blank)
                    .error(R.drawable.ic_blank)
                    .into(binding.imgMenu)

                namaMenu.text = dataSemuaMakanan.namaMenu
                val priceValue = dataSemuaMakanan.harga.toDoubleOrNull() ?: 0.0
                val formattedPrice = formatPriceWithIDR(priceValue)
                harga.text = formattedPrice

                root.setOnClickListener {
                    if (!isSelected) {
                        onItemClick.onClickItem(dataSemuaMakanan)
                        selectedItems.add(dataSemuaMakanan)
                    }
                }

                // Set stroke color based on selection status
                if (isSelected) {
                    root.strokeColor = ContextCompat.getColor(root.context, R.color.blue)
                } else {
                    root.strokeColor = Color.TRANSPARENT // Set default stroke color
                }

                // Observe the selectedItemViewModel data
                checkoutViewModel.dataList.observeForever { selectedItemList ->
                    val selectedItem =
                        selectedItemList?.find { it.id_menu == dataSemuaMakanan.idMenu }
                    root.strokeColor = if (selectedItem != null) {
                        ContextCompat.getColor(root.context, R.color.blue)
                    } else {
                        Color.TRANSPARENT // Set default stroke color
                    }
                    root.isClickable = selectedItem == null
                }


            }
        }
        
    }

    private fun formatPriceWithIDR(price: Double): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = '.'

        val formatter = DecimalFormat("#,###", symbols)
        val formattedPrice = formatter.format(price)

        return "IDR $formattedPrice"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding =
            ItemMenuDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.bind(data, selectedItems.contains(data))
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface OnClickListener {
        fun onClickItem(dataSemuaMakanan: DataSemuaMakanan)
    }
}