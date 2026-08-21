package com.pemula.ramadhandigital.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.pemula.ramadhandigital.*
import com.pemula.ramadhandigital.adapter.MenuAdapter
import com.pemula.ramadhandigital.databinding.FragmentBerandaGuruBinding
import com.pemula.ramadhandigital.databinding.ItemInspirationBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

data class InspirationGuru(val quote: String, val source: String)

class FragmentBerandaGuru : Fragment() {

    private var _binding: FragmentBerandaGuruBinding? = null
    private val binding get() = _binding!!
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val namaUser = Account.Nama ?: "Pembimbing"
        binding.tvGreeting.text = "Assalamu'alaikum\nPembimbing, $namaUser"

        setupInspirationSlider()
        setupMenuRecyclerView()
    }

    private fun setupMenuRecyclerView() {
        // MENU UTAMA GURU: Disamakan dengan Beranda Siswa (Juz Amma, Sholat, Dzikir, Tausiah) 🚀🔥
        val menuList = arrayListOf(
            MenuItem(R.drawable.quran, "Juz Amma"),
            MenuItem(R.drawable.salat, "Bacaan Sholat"),
            MenuItem(R.drawable.zikir, "Dzikir"),
            MenuItem(R.drawable.icon1, "Tausiah")
        )

        val menuAdapter = MenuAdapter(menuList) { item ->
            val intent = when (item.title) {
                "Juz Amma" -> Intent(requireContext(), JuzAmmaActivity::class.java)
                "Bacaan Sholat" -> Intent(requireContext(), BacaanSholatActivity::class.java)
                "Dzikir" -> Intent(requireContext(), DzikirActivity::class.java)
                "Tausiah" -> Intent(requireContext(), TausiahActivity::class.java)
                else -> null
            }

            intent?.let {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(it, options.toBundle())
            }
        }
        binding.rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMenu.setHasFixedSize(true)
        binding.rvMenu.adapter = menuAdapter
    }

    private fun setupInspirationSlider() {
        val list = listOf(
            InspirationGuru("Sesungguhnya bersama kesulitan ada kemudahan.", "QS. Al-Insyirah : 5–6"),
            InspirationGuru("Maka nikmat Tuhanmu yang manakah yang kamu dustakan?", "QS. Ar-Rahman : 13"),
            InspirationGuru("Barangsiapa bertakwa kepada Allah, niscaya Dia memberi jalan keluar.", "QS. At-Talaq : 2")
        )
        binding.vpInspiration.adapter = InspirationGuruAdapter(list)
        
        binding.tlIndicator.removeAllTabs()
        repeat(list.size) { binding.tlIndicator.addTab(binding.tlIndicator.newTab()) }
        
        sliderRunnable = Runnable {
            if (_binding != null) {
                binding.vpInspiration.currentItem += 1
            }
        }
        
        binding.vpInspiration.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tlIndicator.getTabAt(position % list.size)?.select()
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class InspirationGuruAdapter(private val list: List<InspirationGuru>) : androidx.recyclerview.widget.RecyclerView.Adapter<InspirationGuruAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemInspirationBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ItemInspirationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position % list.size]
        holder.binding.tvQuote.text = item.quote
        holder.binding.tvSource.text = item.source
    }
    override fun getItemCount() = Int.MAX_VALUE
}
