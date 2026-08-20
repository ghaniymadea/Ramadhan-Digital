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

        if (Account.isGuru()) {
            // TAB PESRAM GURU: Berisi 4 Menu Management Utama 🍌🐒
            listMenu.add(MenuItem(R.drawable.quran, "ACCEPT SETORAN HAFALAN"))
            listMenu.add(MenuItem(R.drawable.icon1, "TRACKING KEGIATAN SISWA"))
            listMenu.add(MenuItem(R.drawable.mosque, "EKSPOR KE PDF"))
            listMenu.add(MenuItem(R.drawable.salat, "ABSENSI"))
        } else {
            // TAB PESRAM SISWA: Berisi 4 Menu Catatan Kegiatan Pesantren 👦🔥
            listMenu.add(MenuItem(R.drawable.salat, "APRESIASI IBADAH HARIAN"))
            listMenu.add(MenuItem(R.drawable.icon2, "APRESIASI IBADAH SUNNAH RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.mosque, "CATATAN KEGIATAN PESANTREN RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.quran, "SETORAN HAFALAN"))
        }

        val adapter = MenuAdapter(listMenu) { item ->
            when (item.title) {
                // Navigasi Guru 🐒🔥
                "ABSENSI" -> startActivity(Intent(requireContext(), AbsensiActivity::class.java))
                "ACCEPT SETORAN HAFALAN" -> startActivity(Intent(requireContext(), AcceptSetoranActivity::class.java))
                "TRACKING KEGIATAN SISWA" -> startActivity(Intent(requireContext(), TrackingSiswaActivity::class.java))
                "EKSPOR KE PDF" -> startActivity(Intent(requireContext(), ExportPdfActivity::class.java))

                // Navigasi Siswa 👦🚀
                "APRESIASI IBADAH HARIAN" -> startActivity(Intent(requireContext(), IbadahHarianActivity::class.java))
                "APRESIASI IBADAH SUNNAH RAMADHAN" -> startActivity(Intent(requireContext(), IbadahSunnahActivity::class.java))
                "SETORAN HAFALAN" -> startActivity(Intent(requireContext(), SetoranHafalanActivity::class.java))
                "CATATAN KEGIATAN PESANTREN RAMADHAN" -> {
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
