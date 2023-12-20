package id.ac.ukdw.pointofsale.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.data.SidebarItem

class SidebarAdapter(private val items: List<SidebarItem>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private var selectedItemPosition = 0 // Initialize with no selection

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sidebar_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        with(holder.itemView) {
            findViewById<ImageView>(R.id.iconImageView).setImageResource(if (position == selectedItemPosition) item.selectedImageResId else item.iconResId)
            findViewById<TextView>(R.id.textTextView).text = item.text

            // Change background and text color based on selection
            if (position == selectedItemPosition) {
                setBackgroundResource(R.drawable.selected_side_btn)
                findViewById<TextView>(R.id.textTextView).setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                setBackgroundResource(R.drawable.unselected_side_btn)
                findViewById<TextView>(R.id.textTextView).setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            setOnClickListener {
                val previousSelected = selectedItemPosition
                if (selectedItemPosition != holder.adapterPosition) {
                    selectedItemPosition = holder.adapterPosition
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedItemPosition)

                    onItemClick(selectedItemPosition)
                }
                // Add an else block if you want to handle something when the same item is clicked again
            }
        }
    }

    override fun getItemCount(): Int = items.size
}


