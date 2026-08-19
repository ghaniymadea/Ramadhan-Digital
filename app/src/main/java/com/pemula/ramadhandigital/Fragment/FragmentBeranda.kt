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
import androidx.recyclerview.widget.RecyclerView
import com.pemula.ramadhandigital.R
import com.pemula.ramadhandigital.JuzAmmaActivity
import com.pemula.ramadhandigital.BacaanSholatActivity
import com.pemula.ramadhandigital.DzikirActivity
import com.pemula.ramadhandigital.TausiahActivity
import com.pemula.ramadhandigital.adapter.MenuAdapter
import com.pemula.ramadhandigital.databinding.FragmentBerandaBinding
import com.pemula.ramadhandigital.databinding.ItemInspirationBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

data class Inspiration(val quote: String, val source: String)

class FragmentBeranda : Fragment() {
    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGreeting.text = "Assalamu'alaikum, ${Account.Nama ?: "User"}"
        setupInspirationSlider()
        setupMenuRecyclerView()
    }

    private fun setupMenuRecyclerView() {
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
                "Tausiah" -> Intent(requireContext(), TausiahActivity::class.java) // ARAHKAN KE HALAMAN TAUSIAH! 🍌🔥
                else -> null
            }
            intent?.let {
                val options = ActivityOptions.makeCustomAnimation(requireContext(), android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(it, options.toBundle())
            }
        }
        binding.rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMenu.adapter = menuAdapter
    }

    private fun setupInspirationSlider() {
        val list = listOf(
            Inspiration("Sesungguhnya bersama kesulitan ada kemudahan.", "QS. Al-Insyirah : 5–6"),
            Inspiration("Maka nikmat Tuhanmu yang manakah yang kamu dustakan?", "QS. Ar-Rahman : 13"),
            Inspiration("Barangsiapa bertakwa kepada Allah, niscaya Dia memberi jalan keluar.", "QS. At-Talaq : 2")
        )
        binding.vpInspiration.adapter = InspirationAdapter(list)
        binding.tlIndicator.removeAllTabs()
        for (i in list.indices) binding.tlIndicator.addTab(binding.tlIndicator.newTab())
        sliderRunnable = Runnable {
            binding.vpInspiration.currentItem = binding.vpInspiration.currentItem + 1
            sliderHandler.postDelayed(sliderRunnable, 5000)
        }
        binding.vpInspiration.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tlIndicator.getTabAt(position % list.size)?.select()
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })
    }

    override fun onResume() { super.onResume(); sliderHandler.postDelayed(sliderRunnable, 5000) }
    override fun onPause() { super.onPause(); sliderHandler.removeCallbacks(sliderRunnable) }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class InspirationAdapter(private val list: List<Inspiration>) : RecyclerView.Adapter<InspirationAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemInspirationBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ItemInspirationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position % list.size]
        holder.binding.tvQuote.text = item.quote
        holder.binding.tvSource.text = item.source
    }
    override fun getItemCount() = Int.MAX_VALUE
}
