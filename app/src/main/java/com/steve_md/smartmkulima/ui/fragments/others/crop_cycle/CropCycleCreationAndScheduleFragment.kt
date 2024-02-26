package com.steve_md.smartmkulima.ui.fragments.others.crop_cycle

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.steve_md.smartmkulima.R
import com.steve_md.smartmkulima.databinding.FragmentCropCycleCreationAndScheduleBinding
import com.steve_md.smartmkulima.model.CropCycleTask
import com.steve_md.smartmkulima.utils.toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CropCycleCreationAndScheduleFragment : Fragment() {

    private lateinit var binding: FragmentCropCycleCreationAndScheduleBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val startDate = Calendar.getInstance()
    private val endDate = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCropCycleCreationAndScheduleBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        initBinding()
        initSchedulaCropCycleTask()
    }
    private fun initSchedulaCropCycleTask() {
        binding.buttonGenerateSchedule.setOnClickListener { generateSchedule() }
    }

    private fun initBinding() {
        binding.textViewTaskStartDate.setOnClickListener { showDatePickerDialog(true) }
        binding.textViewTaskEndDate.setOnClickListener { showDatePickerDialog(false) }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) startDate else endDate

        val datePickerDialog = DatePickerDialog(requireActivity().applicationContext,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                showTimePicker(isStartDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker(isStartDate: Boolean) {

        val calendar = if (isStartDate) startDate else endDate

        val timePickerDialog = TimePickerDialog(requireActivity().applicationContext,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val formattedDateTime = sdf.format(calendar.time)

                if (isStartDate) {
                    binding.textViewTaskStartDate.text = "Start Date: $formattedDateTime"
                } else {
                    binding.textViewTaskEndDate.text = "End Date: $formattedDateTime"
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )

        timePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun generateSchedule() {
        val taskName: String = binding.inputTask.text.toString()
        val selectedCrop: String = binding.spinnerSelectCrops.selectedItem.toString()
        val startDate: Date? =
            SimpleDateFormat("MM/dd/yyyy").parse(binding.textViewTaskStartDate.text.toString())
        val endDate: Date? =
            SimpleDateFormat("MM/dd/yyyy").parse(binding.textViewTaskEndDate.text.toString())
        val farmInputRequired: String = binding.inputFarmInputNeeded.text.toString()
        val taskPriority: String = binding.spinnerSelectTaskPriority.selectedItem.toString()

        val task = CropCycleTask(
            taskName,
            selectedCrop, startDate ?: Date(),
            endDate ?: Date(), taskPriority, farmInputRequired, taskStatus = CropCycleTask.TaskStatus.valueOf("")
        )

        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference.child("crop_cycle_tasks").push().setValue(task)
        Log.d(this.tag,"$task {} {} {} {}")
        toast("Successfully new generated crop cycle task for crop $selectedCrop")
    }
}