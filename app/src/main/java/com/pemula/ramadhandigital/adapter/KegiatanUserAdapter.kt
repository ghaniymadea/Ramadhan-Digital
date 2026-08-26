package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.R
import com.pemula.ramadhandigital.databinding.ItemKegiatanBinding
import com.pemula.ramadhandigital.model.KegiatanUser

class KegiatanUserAdapter(
    private val list: List<KegiatanUser>,
    private val isGuruMode: Boolean = false,
    private val onDeleteClick: ((Int) -> Unit)? = null,
    private val onClick: (KegiatanUser) -> Unit
) : RecyclerView.Adapter<KegiatanUserAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemKegiatanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKegiatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {
            tvJudul.text = item.kegiatan?.judul ?: "Kegiatan Ramadhan"
            tvPemateri.text = item.kegiatan?.pemateri ?: "Ustadz Pembimbing"
            tvJam.text = item.kegiatan?.jam ?: "--:--"
            badgeTanggal.text = item.kegiatan?.tanggal ?: "-"

            if (isGuruMode) {
                tvNote.visibility = View.GONE
                root.setOnLongClickListener {
                    item.kegiatan?.id?.let { id -> onDeleteClick?.invoke(id) }
                    true
                }
            } else {
                tvNote.visibility = View.VISIBLE
                if (item.note.isNullOrEmpty()) {
                    tvNote.text = "• Belum diisi"
                    tvNote.setBackgroundResource(R.drawable.bg_pill_gray)
                } else {
                    tvNote.text = "• Sudah diisi"
                    tvNote.setBackgroundResource(R.drawable.bg_pill_yellow)
                }
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}