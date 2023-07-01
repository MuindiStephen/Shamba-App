package com.steve_md.smartmkulima.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.steve_md.smartmkulima.databinding.FragmentFarmEquipmentDetailsBinding

class FarmEquipmentDetailsFragment : Fragment() {

    private lateinit var binding:FragmentFarmEquipmentDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFarmEquipmentDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}