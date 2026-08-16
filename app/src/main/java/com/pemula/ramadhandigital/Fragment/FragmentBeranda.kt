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
import com.pemula.ramadhandigital.databinding.FragmentBerandaBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

class FragmentBeranda : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var menuList: ArrayList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val namaUser = Account.Nama ?: "Siswa"
        binding.tvGreeting.text = "Assalamu'alaikum, $namaUser"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // MENU BERANDA SISWA: Pesram dihapus, diganti Tausiah 🍌🐒
        menuList = arrayListOf(
            MenuItem(R.drawable.quran, "Juz Amma"),
            MenuItem(R.drawable.salat, "Bacaan Sholat"),
            MenuItem(R.drawable.zikir, "Dzikir"),
            MenuItem(R.drawable.sermon, "Tausiah")
        )

        menuAdapter = MenuAdapter(menuList) { item ->
            when (item.title) {
                "Juz Amma" -> startActivity(Intent(requireContext(), JuzAmmaActivity::class.java))
                "Bacaan Sholat" -> startActivity(Intent(requireContext(), BacaanSholatActivity::class.java))
                "Dzikir" -> startActivity(Intent(requireContext(), DzikirActivity::class.java))
                "Tausiah" -> {
                    val intent = Intent(requireContext(), KegiatanUserActivity::class.java)
                    intent.putExtra("KATEGORI", "CATATAN TAUSIAH")
                    startActivity(intent)
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
