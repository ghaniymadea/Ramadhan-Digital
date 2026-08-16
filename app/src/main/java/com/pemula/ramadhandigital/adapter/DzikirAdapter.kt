package com.pemula.ramadhandigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemDzikirBinding
import com.pemula.ramadhandigital.model.Dzikir

class DzikirAdapter(
    private val list: List<Dzikir>
) : RecyclerView.Adapter<DzikirAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDzikirBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDzikirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvJudulDzikir.text = item.nama
            tvDzikirArab.text = item.arabic
            tvDzikirLatin.text = item.terjemah
        }
    }

    override fun getItemCount(): Int = list.size
}