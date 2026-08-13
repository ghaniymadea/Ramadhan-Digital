package com.pemula.ramadhandigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.fragment.FragmentBeranda
import com.pemula.ramadhandigital.databinding.ActivityBerandaBinding

class BerandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan Fragment Beranda pertama kali
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentBeranda())
                .commit()
        }
    }
}