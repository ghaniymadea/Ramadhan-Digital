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
            // UNTUK GURU: Tab Pesram berisi menu Ibadah Umum agar Pembimbing tetap istiqomah 🍌🐒
            listMenu.add(MenuItem(R.drawable.quran, "Juz Amma"))
            listMenu.add(MenuItem(R.drawable.salat, "Bacaan Sholat"))
            listMenu.add(MenuItem(R.drawable.zikir, "Dzikir"))
            listMenu.add(MenuItem(R.drawable.icon1, "Tausiah"))
        } else {
            // UNTUK SISWA: Tab Pesram berisi 4 Menu Catatan Kegiatan Utama 👦🔥
            listMenu.add(MenuItem(R.drawable.salat, "CATATAN APRESIASI IBADAH HARIAN"))
            listMenu.add(MenuItem(R.drawable.salat, "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.mosque, "CATATAN KEGIATAN PESANTREN RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.quran, "SETORAN HAFALAN"))
        }

        val adapter = MenuAdapter(listMenu) { item ->
            when (item.title) {
                // Navigasi Ibadah Umum 📖
                "Juz Amma" -> startActivity(Intent(requireContext(), JuzAmmaActivity::class.java))
                "Bacaan Sholat" -> startActivity(Intent(requireContext(), BacaanSholatActivity::class.java))
                "Dzikir" -> startActivity(Intent(requireContext(), DzikirActivity::class.java))
                "Tausiah" -> {
                    val intent = Intent(requireContext(), KegiatanUserActivity::class.java)
                    intent.putExtra("KATEGORI", "CATATAN TAUSIAH")
                    startActivity(intent)
                }
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
