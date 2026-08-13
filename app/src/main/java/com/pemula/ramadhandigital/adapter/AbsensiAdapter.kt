package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemAbsensiBinding
import com.pemula.ramadhandigital.model.AbsensiItem

class AbsensiAdapter(
    private val list: List<AbsensiItem>
) : RecyclerView.Adapter<AbsensiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAbsensiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvNamaSiswa.text = item.namaSiswa
            tvStatusSekarang.text = "Status: ${item.statusAbsensi}"

            // Reset status radio button sesuai data dari API
            when (item.idStatusAbsensi) {
                1 -> rbHadir.isChecked = true
                3 -> rbSakit.isChecked = true
                2 -> rbIzin.isChecked = true
                4 -> rbAlpa.isChecked = true
                else -> rgStatus.clearCheck()
            }

            // Simpan perubahan status saat guru klik radio button 🍌
            rgStatus.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    com.pemula.ramadhandigital.R.id.rbHadir -> item.idStatusAbsensi = 1
                    com.pemula.ramadhandigital.R.id.rbSakit -> item.idStatusAbsensi = 3
                    com.pemula.ramadhandigital.R.id.rbIzin -> item.idStatusAbsensi = 2
                    com.pemula.ramadhandigital.R.id.rbAlpa -> item.idStatusAbsensi = 4
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}