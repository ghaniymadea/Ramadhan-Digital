package com.pemula.ramadhandigital.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.LoginActivity
import com.pemula.ramadhandigital.SessionManager
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.databinding.FragmentProfileBinding
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.launch

class FragmentProfile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val setoranController = SetoranHafalanController()
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

        // Stats hanya untuk Siswa 👦
        if (Account.Role != "1") {
            loadStudentStats()
        } else {
            // Label kartu untuk Guru
            binding.tvStreak.text = "GURU"
            binding.tvLastJuz.text = "AKTIF"
        }

        binding.btnLogoutCard.setOnClickListener {
            performLogout()
        }
    }

    private fun setupProfileInfo() {
        val roleLabel = if (Account.Role == "1") "Pembimbing" else "Siswa"

        binding.tvProfileNama.text = Account.Nama ?: "User Ramadhan"
        binding.tvProfileLokasi.text = "$roleLabel - ${Account.Kelas ?: "Umum"}"
    }

    private fun loadStudentStats() {
        lifecycleScope.launch {
            try {
                val listSetoran = setoranController.getSetoranByUser(Account.Id)
                if (!listSetoran.isNullOrEmpty()) {
                    val latest = listSetoran.first()
                    binding.tvLastJuz.text = "Juz ${latest.surah?.nomor ?: "-"}"
                    binding.tvStreak.text = "${listSetoran.size} Setoran"
                }
            } catch (e: Exception) {
                binding.tvLastJuz.text = "-"
                binding.tvStreak.text = "0 Hari"
            }
        }
    }

    private fun performLogout() {
        // PAKAI SESSION MANAGER BUAT HAPUS SEMUANYA! 🐒💨
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
