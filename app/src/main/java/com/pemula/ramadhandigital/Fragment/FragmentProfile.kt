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
        val roleLabel = if (Account.isGuru()) "Pembimbing" else "Siswa"
        
        // 1. OTOMATIS AMBIL HURUF DEPAN NAMA UNTUK AVATAR 🍌🐒
        binding.tvAvatarInitial.text = Account.Nama?.trim()?.take(1)?.uppercase() ?: "U"
        
        // 2. TAMPILKAN NAMA LENGKAP 🚀
        binding.tvFullName.text = Account.Nama ?: "User Ramadhan"
        
        binding.tvProfileRoleLabel.text = "$roleLabel (${Account.Username ?: ""})"

        // Bagian Informasi Personal 🐒✨
        binding.tvTahunAjaran.text = "2026/2027"
        
        val kelas = Account.Kelas ?: "-"
        binding.tvKelasDiampu.text = kelas
        
        binding.tvTingkat.text = when {
            kelas.contains("XII", ignoreCase = true) -> "XII"
            kelas.contains("XI", ignoreCase = true) -> "XI"
            kelas.contains("X", ignoreCase = true) -> "X"
            else -> "-"
        }
    }

    private fun performLogout() {
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
