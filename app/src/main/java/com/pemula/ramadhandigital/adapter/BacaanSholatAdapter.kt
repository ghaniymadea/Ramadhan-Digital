package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemBacaanSholatBinding
import com.pemula.ramadhandigital.model.BacaanSholat

class BacaanSholatAdapter(
    private val list: List<BacaanSholat>
) : RecyclerView.Adapter<BacaanSholatAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBacaanSholatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBacaanSholatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // MONYET PASANG SEMUA DATA KE ID BARU! 🍌🐒
            tvNomor.text = (item.urutan ?: (position + 1)).toString()
            tvJudul.text = item.nama ?: "Bacaan Sholat"
            tvArabic.text = item.arabic ?: "-"
            tvTerjemahan.text = item.translate ?: "-" // Pasang terjemahan di sini
        }
    }

    override fun getItemCount(): Int = list.size
}
