package com.pemula.ramadhandigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pemula.ramadhandigital.fragment.FragmentBerandaGuru
import com.pemula.ramadhandigital.fragment.FragmentPesram
import com.pemula.ramadhandigital.fragment.FragmentProfile
import com.pemula.ramadhandigital.databinding.ActivityBerandaGuruBinding

class BerandaGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBerandaGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan Beranda Guru saat pertama buka 🍌
        if (savedInstanceState == null) {
            replaceFragment(FragmentBerandaGuru())
        }

        // MONYET ATUR NAVIGASI GURU! 🐒🔥
        binding.bottomNavigationGuru.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda_guru -> {
                    replaceFragment(FragmentBerandaGuru())
                    true
                }
                R.id.nav_pesram_guru -> {
                    replaceFragment(FragmentPesram()) // FragmentPesram sekarang pintar, bisa tau siapa yang buka!
                    true
                }
                R.id.nav_profile_guru -> {
                    replaceFragment(FragmentProfile())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerGuru, fragment)
            .commit()
    }
}
