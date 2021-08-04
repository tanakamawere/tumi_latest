package org.tmz.tumi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tmz.tumi.Finances.RecordSaleActivity
import org.tmz.tumi.Main.Dashboard.DashboardActivity
import org.tmz.tumi.Main.Explore.ExploreActivity
import org.tmz.tumi.databinding.FragmentSellProductsBinding


class FragmentSellProducts : Fragment() {
    private val TAG = "FragmentSellProducts"

    private var _binding: FragmentSellProductsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSellProductsBinding.inflate(inflater, container, false)

        val bottomNavigationView: BottomNavigationView? =
            binding?.bottomBar?.bottomNavigationViewMain
        bottomNavigationView?.menu?.findItem(R.id.sell)?.isChecked = true
        bottomNavigationView?.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.dashboard -> startActivity(
                    Intent(
                        requireContext(),
                        DashboardActivity::class.java
                    )
                )
                R.id.explore -> startActivity(Intent(requireContext(), ExploreActivity::class.java))
                R.id.sell -> startActivity(Intent(requireContext(), RecordSaleActivity::class.java))
            }
            true
        }
        return binding?.root
    }
}