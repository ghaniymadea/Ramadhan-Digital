package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemAcceptSetoranBinding
import com.pemula.ramadhandigital.model.SetoranHafalan

class AcceptSetoranAdapter(
    private val list: List<SetoranHafalan>,
    private val onAccept: (SetoranHafalan) -> Unit
) : RecyclerView.Adapter<AcceptSetoranAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAcceptSetoranBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAcceptSetoranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // Tampilkan data siswa (ID User sementara, nanti bisa di-join di backend) 🍌
            tvNamaSiswa.text = "Siswa ID: ${item.idUser}"
            tvSurah.text = "Surah: ${item.surah?.surahName ?: "ID Surah: ${item.idSurah}"}"
            tvNote.text = "Note: ${item.note ?: "-"}"
            
            btnAccept.setOnClickListener { onAccept(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
