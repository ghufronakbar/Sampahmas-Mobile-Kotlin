package com.example.sampahmasgabungan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class MyOrders : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapterMyorders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        viewPager2 = findViewById(R.id.view_pager)

        // Mendapatkan informasi dari Intent
        val intent = intent
        val tabToSelect = intent.getIntExtra("tabToSelect", 0) // Nilai default adalah 0 untuk tab Unpaid

        // Pilih tab berdasarkan informasi yang diterima
        viewPager2.post {
            viewPager2.currentItem = tabToSelect
        }

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }

        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)

        adapter = FragmentPageAdapterMyorders(supportFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Unpaid"))
        tabLayout.addTab(tabLayout.newTab().setText("Packaging"))
        tabLayout.addTab(tabLayout.newTab().setText("Delivery"))
        tabLayout.addTab(tabLayout.newTab().setText("Rating"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}