package com.pemula.ramadhandigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pemula.ramadhandigital.fragment.FragmentBeranda
import com.pemula.ramadhandigital.fragment.FragmentPesram
import com.pemula.ramadhandigital.fragment.FragmentProfile
import com.pemula.ramadhandigital.databinding.ActivityBerandaBinding

class BerandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan Fragment Beranda sebagai menu utama saat pertama buka 🍌
        if (savedInstanceState == null) {
            replaceFragment(FragmentBeranda())
        }

        // MONYET ATUR KLIK NAVBAR DI SINI! 🐒🔥
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    replaceFragment(FragmentBeranda())
                    true
                }
                R.id.nav_pesram -> {
                    replaceFragment(FragmentPesram())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(FragmentProfile())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
