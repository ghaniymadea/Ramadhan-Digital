package com.pemula.ramadhandigital.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pemula.ramadhandigital.LoginActivity
import com.pemula.ramadhandigital.databinding.FragmentProfileBinding
import com.pemula.ramadhandigital.model.Account

class FragmentProfile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan data profil dari Account object 🍌
        binding.tvProfileNama.text = Account.Nama ?: "User"
        binding.tvProfileRole.text = "Siswa - ${Account.Kelas ?: ""}"

        binding.btnLogout.setOnClickListener {
            // Hapus session (simpelnya kosongkan data Account) 🐒
            Account.Id = 0
            Account.Token = null
            
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
