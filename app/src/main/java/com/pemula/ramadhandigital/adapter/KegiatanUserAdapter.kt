package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.R
import com.pemula.ramadhandigital.databinding.ItemKegiatanBinding
import com.pemula.ramadhandigital.model.KegiatanUser

class KegiatanUserAdapter(
    private val list: List<KegiatanUser>,
    private val onClick: (KegiatanUser) -> Unit
) : RecyclerView.Adapter<KegiatanUserAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemKegiatanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKegiatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // MONYET NGINTIP KE DALEM KOTAK 'kegiatan' BUAT AMBIL INFO! 🍌🐒
        holder.binding.apply {
            tvJudul.text = item.kegiatan?.judul ?: "Kegiatan Ramadhan"
            tvPemateri.text = item.kegiatan?.pemateri ?: "Ustadz Pembimbing"
            tvJam.text = item.kegiatan?.jam ?: "--:--"

            // Pasang gambar masjid kalau gak ada gambarnya
            val icon = if (item.kegiatan?.gambar != null && item.kegiatan.gambar != 0) {
                item.kegiatan.gambar
            } else {
                R.drawable.mosque
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}