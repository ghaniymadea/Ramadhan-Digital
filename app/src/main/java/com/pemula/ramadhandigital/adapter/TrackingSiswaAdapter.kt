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
            tvNamaSiswa.text = item.namaUser ?: "Tanpa Nama"
            tvKelas.text = "Siswa Ramadhan"
            
            // Tampilkan status ringkasan di badge 🚀
            val sholatDone = item.detailSholatWajibs?.count { it.idStatusSholatWajib == 1 || it.idStatusSholatWajib == 2 } ?: 0
            tvStatusAbsensi.text = "Sholat: $sholatDone/5"
            
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
