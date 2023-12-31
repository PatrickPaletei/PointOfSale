package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.data.CheckOutData

class CheckOutAdapter(
    private val incrementClickListener: (Int) -> Unit,
    private val decrementClickListener: (Int) -> Unit,
    private val ubahSemuaClickListener: (CheckOutData) -> Unit
) : RecyclerView.Adapter<CheckOutAdapter.CardViewHolder>() {

    private var dataList: List<CheckOutData> = emptyList() // Initialize with empty list

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val judulMenu: TextView = itemView.findViewById(R.id.judulMenu)
        val hargaMenu: TextView = itemView.findViewById(R.id.harga)
        val jumlahMenu: TextView = itemView.findViewById(R.id.jumlahMenu)
        val catatanMenu: TextView = itemView.findViewById(R.id.catatanMenu)
        val containerCatatan: LinearLayout = itemView.findViewById(R.id.ubahCatatan)
        val tambahMenu: ImageButton = itemView.findViewById(R.id.tambahMenu)
        val kurangMenu: ImageButton = itemView.findViewById(R.id.kurangMenu)
        val ubahSemua: TextView = itemView.findViewById(R.id.ubahMenu)
        val image:ImageView = itemView.findViewById(R.id.fotoMenu)
        init {
            tambahMenu.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    incrementClickListener(position)
                }
            }
            kurangMenu.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    decrementClickListener(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_checkout, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.judulMenu.text = currentItem.judulMenu
        holder.hargaMenu.text = currentItem.harga
        holder.jumlahMenu.text = currentItem.jumlah_pesanan.toString()
        Glide.with(holder.itemView)
            .load(currentItem.image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(6)))
            .placeholder(R.drawable.ic_blank)
            .error(R.drawable.ic_blank)
            .into(holder.image)

        if (currentItem.catatan.isEmpty()) {
            holder.containerCatatan.visibility = View.GONE // Hide catatanMenu if notes is empty or null
        } else {
            holder.containerCatatan.visibility = View.VISIBLE // Show catatanMenu if notes is present
            holder.catatanMenu.text = currentItem.catatan
        }

        holder.ubahSemua.setOnClickListener {
            ubahSemuaClickListener(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // Function to update the adapter's data
    fun updateData(newDataList: List<CheckOutData>) {
        dataList = newDataList
        notifyDataSetChanged()
    }


}
