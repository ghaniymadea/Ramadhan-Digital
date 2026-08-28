package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemTrackingSiswaBinding
import com.pemula.ramadhandigital.model.IbadahHarian

class TrackingSiswaAdapter(
    private val list: List<IbadahHarian>,
    private val onClick: (IbadahHarian) -> Unit
) : RecyclerView.Adapter<TrackingSiswaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTrackingSiswaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackingSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // Set inisial nama (huruf pertama) 👤
            val nama = item.namaUser ?: "Tanpa Nama"
            tvNamaSiswa.text = nama
            tvInitial.text = if (nama.isNotEmpty()) nama.take(1).uppercase() else "?"
            
            tvKelas.text = "Siswa"

            
            // Klik nama/item untuk download PDF Sholat Pribadi
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
