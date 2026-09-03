package com.pemula.ramadhandigital.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemSetoranHafalanBinding
import com.pemula.ramadhandigital.model.SetoranHafalan
import java.text.SimpleDateFormat
import java.util.*

class SetoranHafalanAdapter(
    private val list: List<SetoranHafalan>,
    private val onClick: (SetoranHafalan) -> Unit
) : RecyclerView.Adapter<SetoranHafalanAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSetoranHafalanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSetoranHafalanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvNomor.text = (position + 1).toString()
            
            // Tampilan Nama Materi (Surah atau Bacaan Sholat) 📖
            val title = when {
                item.surah != null -> item.surah.surahName ?: "Surah (ID: ${item.idSurah})"
                item.bacaanSholat != null -> item.bacaanSholat.nama ?: "Bacaan (ID: ${item.idBacaanSholat})"
                item.idSurah != null && item.idSurah != 0 -> "Surah (ID: ${item.idSurah})"
                item.idBacaanSholat != null && item.idBacaanSholat != 0 -> "Bacaan (ID: ${item.idBacaanSholat})"
                else -> "Setoran Hafalan"
            }
            tvSurahName.text = title
            
            // LOGIKA STATUS
            val statusNama = if (item.idStatusSetoranHafalan == 1) "Tuntas" else "Belum Tuntas"
            tvStatus.text = statusNama.uppercase()
            
            val color = if (item.idStatusSetoranHafalan == 1) "#059669" else "#DC2626"
            tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

            // Tanggal
            tvTanggal.text = "📅 ${formatNiceDate(item.tanggalSetoran)}"
            
            // Catatan
            tvNote.text = if (item.note.isNullOrEmpty()) "Belum ada catatan." else "Catatan: ${item.note}"

            root.setOnClickListener { onClick(item) }
        }
    }

    private fun formatNiceDate(dateStr: String?): String {
        if (dateStr == null) return "-"
        return try {
            val cleanDate = if (dateStr.contains("T")) dateStr.split("T")[0] else dateStr
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val output = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = input.parse(cleanDate)
            output.format(date!!)
        } catch (e: Exception) { dateStr ?: "-" }
    }

    override fun getItemCount(): Int = list.size
}
