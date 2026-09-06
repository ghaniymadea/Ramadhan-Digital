package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemIbadahHarianBinding
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.utils.DateHelper

class IbadahHarianAdapter(private var list: List<IbadahHarian>) :
    RecyclerView.Adapter<IbadahHarianAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemIbadahHarianBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIbadahHarianBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvTanggal.text = DateHelper.toDisplayDate(item.tanggal)
            
            // Tampilkan nama user jika ada (biasanya untuk monitoring guru)
            if (!item.namaUser.isNullOrEmpty()) {
                tvNamaUser.text = item.namaUser
                tvNamaUser.visibility = View.VISIBLE
            } else {
                tvNamaUser.visibility = View.GONE
            }

            // Hitung jumlah sholat yang sudah dikerjakan (ID 1: Berjamaah, ID 2: Munfarid)
            val sholatDone = item.detailSholatWajibs?.count {
                it.idStatusSholatWajib == 1 || it.idStatusSholatWajib == 2
            } ?: 0

            tvStatusSholat.text = "$sholatDone/5 Selesai"
            
            // Set warna status sholat (Hijau jika lengkap, Abu jika belum)
            if (sholatDone == 5) {
                tvStatusSholat.setTextColor(android.graphics.Color.parseColor("#059669"))
            } else {
                tvStatusSholat.setTextColor(android.graphics.Color.parseColor("#6B7280"))
            }

            tvStatusQuran.text = if (item.membacaAlquran) "Selesai" else "Belum"
            
            // Set warna status Quran
            if (item.membacaAlquran) {
                tvStatusQuran.setTextColor(android.graphics.Color.parseColor("#059669"))
            } else {
                tvStatusQuran.setTextColor(android.graphics.Color.parseColor("#EF4444"))
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<IbadahHarian>) {
        list = newList
        notifyDataSetChanged()
    }
}
