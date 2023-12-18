package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.api.response.DataMakanan
import id.ac.ukdw.pointofsale.api.response.DataSemuaMakanan
import id.ac.ukdw.pointofsale.databinding.ItemMenuDashboardBinding

class CardAdapterMenu(
    private val onItemClick: OnClickListener
) : RecyclerView.Adapter<CardAdapterMenu.CardViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<DataMakanan>() {
        override fun areItemsTheSame(
            oldItem: DataMakanan, newItem: DataMakanan
        ): Boolean {
            return oldItem.idMenu == oldItem.idMenu
        }

        override fun areContentsTheSame(
            oldItem: DataMakanan,
            newItem: DataMakanan
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitData(value: List<DataMakanan>) = differ.submitList(value)

    inner class CardViewHolder(private val binding: ItemMenuDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Access your views using binding instead of findViewById
        fun bind(dataMakanan: DataMakanan) {
            binding.apply {
                namaMenu.text = dataMakanan.namaMenu
                val formattedHarga = dataMakanan.harga.removeSuffix(".00")
                val hargaWithIDR = "IDR $formattedHarga"
                harga.text = hargaWithIDR
                root.setOnClickListener {
                    onItemClick.onClickItem(dataMakanan)
                }
            }
        }
    }

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
        fun onClickItem(dataMakanan: DataMakanan)
    }
}