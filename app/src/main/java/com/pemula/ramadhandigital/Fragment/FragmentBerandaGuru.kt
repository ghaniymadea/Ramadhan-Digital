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
        // MONYET FIX: Header tegas untuk Panel Pembimbing! 🍌🔥
        binding.tvGreeting.text = "⭐ Panel Pembimbing\nAssalamu'alaikum, $namaUser"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // SESUAI FORMAT PERMINTAAN BOS: 4 MENU MANAGEMENT GURU 🍌🐒
        val menuList = arrayListOf(
            MenuItem(R.drawable.quran, "ACCEPT SETORAN HAFALAN"),
            MenuItem(R.drawable.icon1, "TRACKIING KEGIATAN SISWA"),
            MenuItem(R.drawable.mosque, "EKSPOR KE PDF"),
            MenuItem(R.drawable.salat, "ABSENSI")
        )

        val menuAdapter = MenuAdapter(menuList) { item ->
            when (item.title) {
                "ABSENSI" -> startActivity(Intent(requireContext(), AbsensiActivity::class.java))
                "ACCEPT SETORAN HAFALAN" -> startActivity(Intent(requireContext(), AcceptSetoranActivity::class.java))
                "TRACKIING KEGIATAN SISWA" -> startActivity(Intent(requireContext(), TrackingSiswaActivity::class.java))
                "EKSPOR KE PDF" -> startActivity(Intent(requireContext(), ExportPdfActivity::class.java))
                else -> Toast.makeText(requireContext(), "Fitur ${item.title} segera aktif!", Toast.LENGTH_SHORT).show()
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
