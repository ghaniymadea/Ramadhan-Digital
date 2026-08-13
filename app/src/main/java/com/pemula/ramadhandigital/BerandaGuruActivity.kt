package com.pemula.ramadhandigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.fragment.FragmentBerandaGuru
import com.pemula.ramadhandigital.databinding.ActivityBerandaGuruBinding

class BerandaGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBerandaGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerGuru, FragmentBerandaGuru())
                .commit()
        }
    }
}