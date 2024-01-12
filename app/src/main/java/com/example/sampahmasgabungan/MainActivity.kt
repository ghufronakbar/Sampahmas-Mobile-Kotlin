package com.example.sampahmasgabungan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sampahmasgabungan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    //change1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeView())

        val fragmentToLoad = intent.getStringExtra("fragmentToLoad")

        if (fragmentToLoad == "MarketView") {
            replaceFragment(MarketView())
        }

        binding.BottomNavMenu.setOnItemSelectedListener{item ->

            when(item.itemId){

                R.id.homes -> replaceFragment(HomeView())
                R.id.wish -> replaceFragment(WishView())
                R.id.market -> replaceFragment(MarketView())
                R.id.user -> replaceFragment(ProfileView())

                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}
