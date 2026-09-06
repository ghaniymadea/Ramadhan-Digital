package com.pemula.ramadhandigital.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemRekapAbsensiBinding
import com.pemula.ramadhandigital.model.AbsensiItem

class RekapAbsensiAdapter(
    private var list: List<AbsensiItem>,
    private var isKeseluruhan: Boolean = false
) : RecyclerView.Adapter<RekapAbsensiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRekapAbsensiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRekapAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateData(newList: List<AbsensiItem>, isKeseluruhan: Boolean) {
        this.list = newList
        this.isKeseluruhan = isKeseluruhan
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvNamaSiswa.text = item.namaSiswa
            tvAvatarInitial.text = if (item.namaSiswa.isNotEmpty()) item.namaSiswa.trim().take(1).uppercase() else "?"

            if (isKeseluruhan) {
                // Tampilan Rekap Keseluruhan 📊
                tvInfoSub.text = "Total Kehadiran"
                tvBadgeStatus.visibility = View.GONE
                layoutRekapAngka.visibility = View.VISIBLE
                
                tvHadirCount.text = item.totalHadir.toString()
                tvIzinCount.text = item.totalIzin.toString()
                tvSakitCount.text = item.totalSakit.toString()
                tvAlpaCount.text = item.totalAlpa.toString()
            } else {
                // Tampilan Harian 📅
                tvBadgeStatus.visibility = View.VISIBLE
                layoutRekapAngka.visibility = View.GONE
                
                val status = item.statusAbsensi ?: "Belum Absen"
                tvBadgeStatus.text = status.uppercase()
                tvInfoSub.text = "Status: $status"

                val (color, bgColor) = when (item.idStatusAbsensi) {
                    1 -> "#059669" to "#ECFDF5" // Hadir
                    2 -> "#EAB308" to "#FEFCE8" // Izin
                    3 -> "#3B82F6" to "#EFF6FF" // Sakit
                    4 -> "#DC2626" to "#FEF2F2" // Alpa
                    else -> "#64748B" to "#F1F5F9" // Default
                }
                
                tvBadgeStatus.setTextColor(Color.parseColor(color))
                tvBadgeStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColor))
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
