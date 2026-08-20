package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.R
import com.pemula.ramadhandigital.databinding.ItemTausiahBinding
import com.pemula.ramadhandigital.model.Tausiah

class TausiahAdapter(
    private val list: List<Tausiah>,
    private val onClick: (Tausiah) -> Unit
) : RecyclerView.Adapter<TausiahAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTausiahBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTausiahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvJudul.text = item.judulTausiah ?: "Tausiah"
            tvPenceramah.text = item.namaPenceramah ?: "Ustadz Pembimbing"
            
            // MONYET FIX ID: Pake tvTanggal sesuai layout item_tausiah.xml! 🍌📅
            val tgl = item.tanggal?.take(10) ?: "-"
            tvTanggal.text = "🕒 $tgl"
            
            if (item.isSubmitted) {
                tvStatus.text = "SUBMITTED"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_700))
            } else {
                tvStatus.text = "DRAFT"
                tvStatus.setTextColor(ContextCompat.getColor(root.context, android.R.color.darker_gray))
            }

            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size
}
