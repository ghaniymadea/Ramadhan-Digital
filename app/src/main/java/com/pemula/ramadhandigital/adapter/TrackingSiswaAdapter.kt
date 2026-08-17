package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemTrackingSiswaBinding
import com.pemula.ramadhandigital.model.AbsensiItem

class TrackingSiswaAdapter(
    private val list: List<AbsensiItem>,
    private val onClick: (AbsensiItem) -> Unit
) : RecyclerView.Adapter<TrackingSiswaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTrackingSiswaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackingSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvNamaSiswa.text = item.namaSiswa
            tvKelas.text = "Siswa Aktif" // Bisa disesuaikan dengan data kelas jika ada
            
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
