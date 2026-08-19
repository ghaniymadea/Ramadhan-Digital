package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemSurahBinding
import com.pemula.ramadhandigital.model.Surah

class SurahAdapter(
    private var list: List<Surah>,
    private val onClick: (Surah) -> Unit
) : RecyclerView.Adapter<SurahAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSurahBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // Monyet sesuaikan ID dengan yang ada di item_surah.xml 🍌🐒
            tvNomorSurah.text = item.nomor.toString()
            tvNamaSurah.text = item.surahName
            tvArtiSurah.text = item.artiSurat
            tvTempatTurun.text = item.tempatTurun
            
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size

    // Fungsi sakti buat update data pas difilter! 🍌✨
    fun updateList(newList: List<Surah>) {
        list = newList
        notifyDataSetChanged()
    }
}
