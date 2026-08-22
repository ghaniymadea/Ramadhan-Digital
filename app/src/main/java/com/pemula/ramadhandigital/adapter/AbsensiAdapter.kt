package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.R
import com.pemula.ramadhandigital.databinding.ItemAbsensiBinding
import com.pemula.ramadhandigital.model.AbsensiItem

class AbsensiAdapter(
    private var list: List<AbsensiItem>
) : RecyclerView.Adapter<AbsensiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAbsensiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAbsensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateData(newList: List<AbsensiItem>) {
        this.list = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            // 1. Tampilkan Nama dan Role (NIS sudah dihapus) 🍌🐒
            tvNamaSiswa.text = item.namaSiswa
            tvRoleSiswa.text = item.role ?: "Siswa"
            
            // 2. Set Inisial Nama untuk Avatar
            tvAvatarInitial.text = if (item.namaSiswa.isNotEmpty()) item.namaSiswa.trim().take(1).uppercase() else "?"

            // 3. Reset Listener agar tidak terjadi bug saat scrolling 🔄
            rgStatus.setOnCheckedChangeListener(null)
            rgStatus.clearCheck()

            // 4. SINKRONISASI STATUS: 1=Hadir, 2=Izin, 3=Sakit, 4=Alpa
            when (item.idStatusAbsensi) {
                1 -> rbHadir.isChecked = true
                2 -> rbIzin.isChecked = true
                3 -> rbSakit.isChecked = true
                4 -> rbAlpa.isChecked = true
            }

            // 5. Pasang kembali Listener untuk menangkap perubahan status
            rgStatus.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbHadir -> item.idStatusAbsensi = 1
                    R.id.rbIzin -> item.idStatusAbsensi = 2
                    R.id.rbSakit -> item.idStatusAbsensi = 3
                    R.id.rbAlpa -> item.idStatusAbsensi = 4
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
