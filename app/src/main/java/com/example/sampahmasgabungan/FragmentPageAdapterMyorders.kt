package com.example.sampahmasgabungan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sampahmasgabungan.fragmentsMyOrders.DeliveryFragment
import com.example.sampahmasgabungan.fragmentsMyOrders.PackagingFragment
import com.example.sampahmasgabungan.fragmentsMyOrders.RatingFragment
import com.example.sampahmasgabungan.fragmentsMyOrders.UnpaidFragment

class FragmentPageAdapterMyorders (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UnpaidFragment()
            1 -> PackagingFragment()
            2 -> DeliveryFragment()
            else -> RatingFragment()
        }
    }

}