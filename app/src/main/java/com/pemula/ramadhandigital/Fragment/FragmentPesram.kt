package com.pemula.ramadhandigital.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            // TAB PESRAM GURU: Menu lebih ringkas 🍌🐒
            listMenu.add(MenuItem(R.drawable.quran, "SETORAN HAFALAN"))
            listMenu.add(MenuItem(R.drawable.mosque, "EKSPOR KE PDF")) // Di sini nanti ada statistiknya
            listMenu.add(MenuItem(R.drawable.salat, "ABSENSI"))
        } else {
            // TAB PESRAM SISWA 👦🔥
            listMenu.add(MenuItem(R.drawable.salat, "APRESIASI IBADAH HARIAN"))
            listMenu.add(MenuItem(R.drawable.icon2, "APRESIASI IBADAH SUNNAH RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.mosque, "CATATAN KEGIATAN PESANTREN RAMADHAN"))
            listMenu.add(MenuItem(R.drawable.quran, "SETORAN HAFALAN"))
        }

        val adapter = MenuAdapter(listMenu) { item ->
            when (item.title) {
                "SETORAN HAFALAN" -> {
                    if (Account.isGuru()) {
                        startActivity(Intent(requireContext(), AddSetoranGuruActivity::class.java))
                    } else {
                        startActivity(Intent(requireContext(), SetoranHafalanActivity::class.java))
                    }
                }
                "ABSENSI" -> startActivity(Intent(requireContext(), AbsensiActivity::class.java))
                "EKSPOR KE PDF" -> startActivity(Intent(requireContext(), ExportPdfActivity::class.java))

                "APRESIASI IBADAH HARIAN" -> startActivity(Intent(requireContext(), IbadahHarianActivity::class.java))
                "APRESIASI IBADAH SUNNAH RAMADHAN" -> startActivity(Intent(requireContext(), IbadahSunnahActivity::class.java))
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
