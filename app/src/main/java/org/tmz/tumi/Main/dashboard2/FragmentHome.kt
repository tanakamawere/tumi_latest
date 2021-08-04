package org.tmz.tumi.Main.dashboard2

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tmz.tumi.Finances.RecordSaleActivity
import org.tmz.tumi.Main.Account.SettingsActivity
import org.tmz.tumi.Main.Dashboard.DashboardActivity
import org.tmz.tumi.Main.Explore.ExploreActivity
import org.tmz.tumi.R
import org.tmz.tumi.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    private val TAG = "FragmentHome"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val bottomNavigationView: BottomNavigationView? =
            binding?.bottomBar?.bottomNavigationViewMain
        bottomNavigationView?.menu?.findItem(R.id.dashboard)?.isChecked = true
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

        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuMessagesToolbar -> findNavController().navigate(R.id.action_fragmentHome_to_fragmentMessageList)
            R.id.menuNotificationToolbar -> findNavController().navigate(R.id.action_fragmentHome_to_fragmentNotifications22)
            R.id.menuSettingsToolbar -> startActivity(
                Intent(
                    requireContext(),
                    SettingsActivity::class.java
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "FragmentHome"
    }
}