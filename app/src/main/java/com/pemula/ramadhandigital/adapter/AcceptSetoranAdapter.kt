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
            // 1. TAMPILKAN NAMA SISWA 👦🔥
            // Kita cek di namaSiswa dulu (direct alias), lalu di objek user, baru fallback ke ID
            tvNamaSiswa.text = item.namaSiswa ?: item.user?.nama ?: "Siswa (ID: ${item.idUser})"
            
            // 2. Tampilkan Detail Hafalan
            tvSurah.text = "Hafalan: ${item.surah?.surahName ?: ("ID Surah: " + item.idSurah)}"
            tvNote.text = "Catatan: ${item.note ?: "-"}"
            
            btnAccept.setOnClickListener { onAccept(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
