package org.tmz.tumi.CustomDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.tmz.tumi.R
import org.tmz.tumi.databinding.ModalBottomNavigationExploreBinding

class MBNavigationExplore : BottomSheetDialog() {
    private var _binding: ModalBottomNavigationExploreBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomNavigationExploreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.modalUser?.setOnClickListener {
            findNavController().navigate(R.id.action_MBNavigationExplore_to_fragmentYou)
            dismiss()
        }
        binding?.modalPublicRequests?.setOnClickListener {
            findNavController().navigate(R.id.action_MBNavigationExplore_to_fragmentServices)
            dismiss()
        }
        binding?.modalServices?.setOnClickListener {
            findNavController().navigate(R.id.action_MBNavigationExplore_to_fragmentServices)
            dismiss()
        }
    }
}