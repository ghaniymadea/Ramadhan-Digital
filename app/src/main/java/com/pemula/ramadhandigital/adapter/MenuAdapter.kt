package com.pemula.ramadhandigital

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.databinding.ItemMenuBinding

class MenuAdapter(

    private val list:List<MenuModel>

):RecyclerView.Adapter<MenuAdapter.ViewHolder>(){

    inner class ViewHolder(val binding:ItemMenuBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding=ItemMenuBinding.inflate(

            LayoutInflater.from(parent.context),
            parent,
            false

        )

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item=list[position]

        holder.binding.tvMenu.text=item.nama

        holder.binding.imgMenu.setImageResource(item.gambar)

    }

}