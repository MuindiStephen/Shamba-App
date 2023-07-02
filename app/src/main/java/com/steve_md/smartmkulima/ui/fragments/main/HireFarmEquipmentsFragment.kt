package com.steve_md.smartmkulima.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.steve_md.smartmkulima.R
import com.steve_md.smartmkulima.adapter.FarmEquipmentAdapter
import com.steve_md.smartmkulima.data.remote.FarmEquipmentsApiClient
import com.steve_md.smartmkulima.databinding.FragmentHireFarmEquipmentsBinding
import com.steve_md.smartmkulima.model.FarmEquipment
import com.steve_md.smartmkulima.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber


@AndroidEntryPoint
class HireFarmEquipmentsFragment : Fragment() {

    private lateinit var binding:FragmentHireFarmEquipmentsBinding
    private lateinit var farmEquipmentsRecylerView: RecyclerView
    private lateinit var farmEquipmentsAdapter: FarmEquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHireFarmEquipmentsBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        farmEquipmentsRecylerView = view.findViewById(R.id.farmEquipmentsRecyclerView)

        farmEquipmentsAdapter = FarmEquipmentAdapter(FarmEquipmentAdapter.OnClickListener { farmEquipment ->  
            Timber.i("Farm Equipments: $farmEquipment.name")

            val action = HireFarmEquipmentsFragmentDirections
                .actionHireFarmEquipmentsFragmentToFarmEquipmentDetailsFragment(farmEquipmentItem = farmEquipment)
            findNavController().navigate(action)
        })

       FarmEquipmentsApiClient.api.getAllEquipments().enqueue(object :retrofit2.Callback<ArrayList<FarmEquipment>>{
           override fun onResponse(
               call: Call<ArrayList<FarmEquipment>>,
               response: Response<ArrayList<FarmEquipment>>
           ) {
               if (response.isSuccessful) {
                   farmEquipmentsAdapter.submitList(response.body())
                   farmEquipmentsRecylerView.adapter = farmEquipmentsAdapter
               }
           }
           override fun onFailure(call: Call<ArrayList<FarmEquipment>>, t: Throwable) {
               toast("No available Farm Equipments to hire.${t.localizedMessage}")
           }
       })

        /**
         * Get a List of all available Farm Equipments
         */

    }
    companion object {
        const val TAG = "HireFarmEquipmentsFragment"
    }

}