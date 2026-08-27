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

    // Simpan posisi yang sedang dibuka (expanded) 📖
    private var expandedPosition = -1

    class ViewHolder(val binding: ItemKegiatanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKegiatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val isExpanded = position == expandedPosition

        holder.binding.apply {
            tvJudul.text = item.kegiatan?.judul ?: "Kegiatan Ramadhan"
            tvPemateri.text = item.kegiatan?.pemateri ?: "Ustadz Pembimbing"
            tvJam.text = item.kegiatan?.jam ?: "--:--"
            badgeTanggal.text = item.kegiatan?.tanggal ?: "-"

            tvNote.visibility = View.VISIBLE
            if (item.note.isNullOrEmpty()) {
                tvNote.text = "• Belum ada catatan"
                tvNote.setBackgroundResource(R.drawable.bg_pill_gray)
                tvNote.setTextColor(android.graphics.Color.parseColor("#64748B"))
                tvNote.maxLines = 1 // Jika kosong tetap 1 baris
            } else {
                tvNote.text = "Catatan: ${item.note}"
                tvNote.setBackgroundResource(R.drawable.bg_pill_yellow)
                tvNote.setTextColor(android.graphics.Color.parseColor("#D97706"))
                
                // LOGIKA MELEBAR (EXPAND) 📖✨
                if (isExpanded) {
                    tvNote.maxLines = Int.MAX_VALUE // Tampilkan semua teks
                    tvNote.ellipsize = null
                } else {
                    tvNote.maxLines = 1 // Sembunyikan sebagian
                    tvNote.ellipsize = android.text.TextUtils.TruncateAt.END
                }
            }

            if (isGuruMode && onDeleteClick != null) {
                root.setOnLongClickListener {
                    item.kegiatan?.id?.let { id -> onDeleteClick.invoke(id) }
                    true
                }
            }

            root.setOnClickListener {
                // Toggle expand/collapse pada posisi ini
                val prevExpanded = expandedPosition
                expandedPosition = if (isExpanded) -1 else position
                
                // Beri tahu adapter untuk refresh item yang berubah biar ada animasinya
                notifyItemChanged(prevExpanded)
                notifyItemChanged(expandedPosition)
                
                onClick(item)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
