package com.pemula.ramadhandigital.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pemula.ramadhandigital.*
import com.pemula.ramadhandigital.adapter.MenuAdapter
import com.pemula.ramadhandigital.databinding.FragmentBerandaGuruBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

class FragmentBerandaGuru : Fragment() {

    private var _binding: FragmentBerandaGuruBinding? = null
    private val binding get() = _binding!!

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var menuList: ArrayList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val namaUser = Account.Nama ?: "Pembimbing"
        binding.tvGreeting.text = "🌙 Assalamu'alaikum, $namaUser"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        menuList = ArrayList()
        menuList.add(MenuItem(R.drawable.quran, "Juz Amma"))
        menuList.add(MenuItem(R.drawable.salat, "Absensi Siswa"))
        menuList.add(MenuItem(R.drawable.zikir, "Accept Hafalan"))
        menuList.add(MenuItem(R.drawable.icon1, "Tracking Siswa"))
        menuList.add(MenuItem(R.drawable.mosque, "Ekspor PDF"))

        menuAdapter = MenuAdapter(menuList) { item ->
            when (item.title) {
                "Juz Amma" -> startActivity(Intent(requireContext(), JuzAmmaActivity::class.java))
                "Absensi Siswa" -> startActivity(Intent(requireContext(), AbsensiActivity::class.java))
                "Accept Hafalan", "Tracking Siswa", "Ekspor PDF" -> {
                    Toast.makeText(requireContext(), "Fitur ${item.title} segera hadir!", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(requireContext(), "Membuka ${item.title}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMenu.setHasFixedSize(true)
        binding.rvMenu.adapter = menuAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}