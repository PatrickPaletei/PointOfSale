package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.data.CardData


class CardAdapter(
    private val dataList: List<CardData>,
    private val itemClickListener: (CardData) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_dashboard, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textViewTitle.text = currentItem.namaMenu
        holder.textViewDescription.text = currentItem.hargaMenu

        // Set item click listener
        holder.itemView.setOnClickListener {
            itemClickListener(currentItem) // Invoke the lambda function on item click
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}