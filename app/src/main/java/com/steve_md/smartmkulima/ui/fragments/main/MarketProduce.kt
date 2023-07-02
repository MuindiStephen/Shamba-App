package com.steve_md.smartmkulima.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.steve_md.smartmkulima.adapter.FarmProduceAdapter
import com.steve_md.smartmkulima.databinding.FragmentMarketProduceBinding
import com.steve_md.smartmkulima.utils.snackBar
import com.steve_md.smartmkulima.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class MarketProduce : Fragment() {

    private lateinit var binding: FragmentMarketProduceBinding
    private val mainViewModel by viewModels<MainViewModel>()
    private val farmProduceAdapter by lazy { FarmProduceAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarketProduceBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        subScribeToFarmProduceObserver()
    }

    private fun subScribeToFarmProduceObserver() {
        lifecycleScope.launch {
            mainViewModel.produce.collect { farmProduceState ->
                Timber.e("Farm Produce State: $farmProduceState")


                if (farmProduceState.isLoading) {
                   snackBar("Loading Farm Produce....")
                }

                if (farmProduceState.error != null && !farmProduceState.isLoading) {
                    snackBar("An error has occurred!")
                }
                if (farmProduceState.farmProduce.isNotEmpty() && !farmProduceState.isLoading) {
                    farmProduceAdapter.submitList(farmProduceState.farmProduce)
                    binding.farmProduceRecyclerView.adapter = farmProduceAdapter

                    Timber.i("Fetched successfully: ${farmProduceState.farmProduce}")
                }
            }
        }
    }

}