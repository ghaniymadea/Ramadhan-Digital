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
import com.pemula.ramadhandigital.databinding.FragmentPesramBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

class FragmentPesram : Fragment() {
    private var _binding: FragmentPesramBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPesramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val listMenu = ArrayList<MenuItem>()

        if (Account.Role == "1") {
            // FORMAT KHUSUS PEMBIMBING / GURU SESUAI REQUEST BOS! 🍌🔥
            listMenu.add(MenuItem(R.drawable.quran, "ACCEPT SETORAN HAFALAN"))
            listMenu.add(MenuItem(R.drawable.icon1, "TRACKING KEGIATAN SISWA"))
            listMenu.add(MenuItem(R.drawable.mosque, "EKSPOR KE PDF"))
            listMenu.add(MenuItem(R.drawable.salat, "ABSENSI"))
        } else {
            // FORMAT KHUSUS SISWA 👦
            listMenu.add(MenuItem(R.drawable.salat, "CATATAN APRESIASI IBADAH HARIAN"))
            listMenu.add(MenuItem(R.drawable.icon2, "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.mosque, "CATATAN KEGIATAN PESANTREN RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.quran, "SETORAN HAFALAN"))
        }

        val adapter = MenuAdapter(listMenu) { item ->
            when (item.title) {
                // Navigasi Management Guru 🐒
                "ABSENSI" -> startActivity(Intent(requireContext(), AbsensiActivity::class.java))
                "ACCEPT SETORAN HAFALAN" -> Toast.makeText(requireContext(), "Membuka Daftar Setoran Siswa...", Toast.LENGTH_SHORT).show()
                "TRACKING KEGIATAN SISWA" -> Toast.makeText(requireContext(), "Monitoring Seluruh Siswa...", Toast.LENGTH_SHORT).show()
                "EKSPOR KE PDF" -> Toast.makeText(requireContext(), "Menyiapkan Dokumen Laporan (PDF)...", Toast.LENGTH_SHORT).show()
                
                // Navigasi Catatan Siswa 👦
                else -> {
                    val intent = Intent(requireContext(), KegiatanUserActivity::class.java)
                    intent.putExtra("KATEGORI", item.title)
                    startActivity(intent)
                }
            }
        }

        binding.rvPesram.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPesram.setHasFixedSize(true)
        binding.rvPesram.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
