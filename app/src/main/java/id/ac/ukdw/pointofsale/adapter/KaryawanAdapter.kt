package id.ac.ukdw.pointofsale.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import id.ac.ukdw.pointofsale.R
import id.ac.ukdw.pointofsale.api.response.DataUser
import id.ac.ukdw.pointofsale.api.response.GetUserResponse
import id.ac.ukdw.pointofsale.databinding.ItemKaryawanBinding

class KaryawanAdapter(
    private val data: GetUserResponse,
    private val onItemDeleteClick: (Int, String) -> Unit
) : RecyclerView.Adapter<KaryawanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemKaryawanBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = data.data[position]
        holder.bind(
            dataItem
        )
    }

    override fun getItemCount(): Int {
        return data.data.size
    }

    inner class ViewHolder(private val binding: ItemKaryawanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.hapusKaryawan.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data.data[position]
                    onItemDeleteClick(item.idUser, item.namaKaryawan)
                }
            }
        }

        fun bind(dataItem: DataUser) {
            binding.namaKaryawan.text = dataItem.namaKaryawan
            binding.usernameKaryawan.text = dataItem.username
            val role = dataItem.role
            if (role == "admin"){
                binding.hapusKaryawan.visibility = View.GONE
            }
            // Loading image using Glide
            Glide.with(binding.root.context)
                .load(dataItem.image)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(R.drawable.man)
                .error(R.drawable.man)
                .into(binding.profilePic)
        }
    }

}