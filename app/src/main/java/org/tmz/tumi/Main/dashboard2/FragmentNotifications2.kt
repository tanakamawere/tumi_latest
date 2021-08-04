package org.tmz.tumi.Main.dashboard2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.tmz.tumi.databinding.FragmentNotifications2Binding
import org.tmz.tumi.viewmodel.NotificationsViewModel


class FragmentNotifications2 : Fragment() {

    private var _binding: FragmentNotifications2Binding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotifications2Binding.inflate(inflater, container, false)

        val recyclerView: RecyclerView? = binding?.recyclerViewNotifications
        recyclerView?.isNestedScrollingEnabled = false
        recyclerView?.setHasFixedSize(false)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        NotificationsViewModel(
            requireContext(),
            binding?.progressBarNotifications!!,
            binding?.nullNotifications!!,
            binding?.recyclerViewNotifications!!
        )

        return binding?.root
    }
}