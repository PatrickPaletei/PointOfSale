package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.response.DataSummary
import id.ac.ukdw.pointofsale.api.response.SummaryResponse
import id.ac.ukdw.pointofsale.databinding.ItemPenjualanBinding

class SummaryAdapter(private val data: SummaryResponse) :
    RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPenjualanBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = data.data[position] // Accessing menuList directly from SummaryResponse
        holder.bind(dataItem)
    }

    override fun getItemCount(): Int {
        return data.data.size // Getting size of menuList directly from SummaryResponse
    }

    class ViewHolder(private val binding: ItemPenjualanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataItem: DataSummary) {
            binding.namaMenu.text = dataItem.menu
            val price = dataItem.harga
            val idrPrice = "IDR $price"
            binding.price.text = idrPrice
            val quatitiy = dataItem.jumlah
            val withTerjual = "$quatitiy terjual"
            binding.quantity.text = withTerjual
            // Calculate and accumulate pendapatan for each item
            val pendapatan = dataItem.harga * dataItem.jumlah
            binding.penghasilan.text = "IDR ${pendapatan}"
        }
    }
}



