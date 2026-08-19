package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemAyatBinding
import com.pemula.ramadhandigital.model.Ayat

class AyatAdapter(
    private val list: List<Ayat>
) : RecyclerView.Adapter<AyatAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAyatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // MONYET PASANG DATA KE LAYOUT BARU! 🍌🐒
            tvNomorAyat.text = (item.nomor ?: (position + 1)).toString()
            tvAyatArab.text = item.arab ?: "-"
            tvAyatTerjemahan.text = item.terjemah ?: "-"
        }
    }

    override fun getItemCount(): Int = list.size
}
