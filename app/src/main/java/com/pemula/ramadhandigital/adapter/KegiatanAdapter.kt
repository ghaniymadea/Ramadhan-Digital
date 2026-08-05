package com.pemula.ramadhandigital

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemKegiatanBinding

class KegiatanAdapter(
    private val list: List<KegiatanModel>
) : RecyclerView.Adapter<KegiatanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKegiatanBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemKegiatanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvJudul.text = item.judul
        holder.binding.tvPemateri.text = item.pemateri
        holder.binding.tvJam.text = item.jam
        holder.binding.imgKegiatan.setImageResource(item.gambar)

    }

}