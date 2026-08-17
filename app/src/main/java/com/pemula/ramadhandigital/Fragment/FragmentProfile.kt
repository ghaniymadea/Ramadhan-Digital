package com.pemula.ramadhandigital.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pemula.ramadhandigital.LoginActivity
import com.pemula.ramadhandigital.SessionManager
import com.pemula.ramadhandigital.databinding.FragmentProfileBinding
import com.pemula.ramadhandigital.model.Account

class FragmentProfile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())

        setupProfileInfo()

        binding.btnLogoutCard.setOnClickListener {
            performLogout()
        }
    }

    private fun setupProfileInfo() {
        // MONYET SESUAIKAN DENGAN LAYOUT BARU & DATA BACKEND 🍌🐒
        
        val namaUser = Account.Nama ?: "User"
        // PEMBIMBING ADALAH ROLE 1 SESUAI BACKEND BOS! 🍌🔥
        val roleLabel = if (Account.Role == "1") "Pembimbing" else "Siswa"
        
        // Bagian Header
        binding.tvProfileInitial.text = namaUser
        binding.tvProfileRoleLabel.text = "$roleLabel ${Account.Username ?: ""}"

        // Bagian Informasi Personal (Sesuai Data Backend) 🔥
        binding.tvTahunAjaran.text = "2026/2027"
        
        // Logika sederhana untuk tingkat 🍌
        val kelas = Account.Kelas ?: "-"
        binding.tvTingkat.text = when {
            kelas.contains("X", ignoreCase = true) -> {
                if (kelas.contains("XI", ignoreCase = true)) {
                    if (kelas.contains("XII", ignoreCase = true)) "XII" else "XI"
                } else "X"
            }
            else -> "-"
        }
        
        binding.tvKelasDiampu.text = kelas
    }

    private fun performLogout() {
        // Hapus session biar aman! 🐒💨
        sessionManager.clearSession()
        
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
