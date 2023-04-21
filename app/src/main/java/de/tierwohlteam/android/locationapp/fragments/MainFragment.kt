package de.tierwohlteam.android.locationapp.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.locationapp.R
import de.tierwohlteam.android.locationapp.databinding.FragmentMainBinding
import de.tierwohlteam.android.locationapp.models.Location
import de.tierwohlteam.android.locationapp.models.UWBListener
import de.tierwohlteam.android.locationapp.others.Status
import de.tierwohlteam.android.locationapp.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.btnRecord.text = "Connect"
        binding.btnRecord.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.uwbListener == null) {
                    context?.let { it1 ->
                        connectDialog(it1)
                    }
                    binding.btnRecord.text = "Record"
                } else {
                    if(viewModel.uwbListener!!.recording) {
                        viewModel.uwbListener!!.recording = false;
                        binding.btnRecord.text = "Record"
                    } else {
                        viewModel.deleteLocations()
                        viewModel.uwbListener!!.recording = true;
                        binding.btnRecord.text = "Stop"
                    }
                }
            }
        }

        binding.imageView2.setImageResource(R.drawable.baseball)
        binding.circleView.bringToFront()

        binding.btnReplay.setOnClickListener {
            val smoothingFactor = 5
            lifecycleScope.launch {
                val locationList = viewModel.getLocations()
                if (locationList.isNotEmpty()) {
                    // go over locationsList with a sliding window size 3.
                    // Generate a new list with each location as average of the sliding window
                    val smoothedLocations = locationList.windowed(smoothingFactor,1) { window ->
                        val smoothedX = window.map { it.x }.average().toFloat()
                        val smoothedY = window.map { it.y }.average().toFloat()
                        val timestamp = window[1].timestamp
                        Location(x= smoothedX, y=smoothedY, timestamp=timestamp)
                    }

                    //val scaleX = binding.imageView2.width / locationList.maxOf { it.x }
                    val scaleX = binding.imageView2.width / 10
                    //val scaleY = binding.imageView2.height / locationList.maxOf { it.y }
                    val scaleY = binding.imageView2.height / 14
                    val shiftX = 50
                    val shiftY = 10
                    val startTime = locationList.minOf { it.timestamp }
                    Log.d("MainFragment", "scaleX: $scaleX, scaleY: $scaleY")
                    val animatorList = mutableListOf<Animator>()
                    for (i in 0 until smoothedLocations.size - 1) {
                        // Get the start and end point of the animation
                        // scaled by the scale factors
                        val startPoint = Location(
                            x = smoothedLocations[i].x * scaleX + shiftX,
                            y = smoothedLocations[i].y * scaleY + shiftY,
                            timestamp = smoothedLocations[i].timestamp
                        )
                        val endPoint = Location(
                            x = smoothedLocations[i + 1].x * scaleX + shiftX,
                            y = smoothedLocations[i + 1].y * scaleY + shiftY,
                            timestamp = smoothedLocations[i + 1].timestamp
                        )
                        val animator = ValueAnimator.ofFloat(0f, 1f)
                        animator.duration =
                            endPoint.timestamp - startPoint.timestamp // Set the duration of each animation
                        animator.addUpdateListener { valueAnimator ->
                            val fraction = valueAnimator.animatedFraction
                            val x = startPoint.x + (fraction * (endPoint.x - startPoint.x))
                            val y = startPoint.y + (fraction * (endPoint.y - startPoint.y))
                            // Update the position of the circle
                            binding.circleView.setCircle(x.toFloat(), y.toFloat(), 10f)
                            binding.tvXCoord.text = locationList[i].x.toString()
                            binding.tvYCoord.text = locationList[i].y.toString()
                            val milliseconds = (locationList[i].timestamp - startTime)
                            // convert milliseconds in minutes and seconds
                            val minutes = milliseconds / 60000
                            val seconds = (milliseconds % 60000) / 1000
                            val timeString = "$minutes:$seconds"
                            binding.tvTimestamp.text = timeString
                        }
                        animatorList.add(animator)
                    }
                    // Play all animations in sequence
                    val animatorSet = AnimatorSet()
                    animatorSet.playSequentially(animatorList)
                    animatorSet.start()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToObservers() {
        //Did the insert work?
        lifecycleScope.launch {
           viewModel.deleteLocationFlow.collect {
                when (it.status) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            it.message ?: "Insert Error",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.btn_record)
                            .show()
                    }

                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Locations deleted, Start recording",
                            //resources.getString(R.string.saved_rating),
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.btn_record)
                            .show()
                    }

                    else -> { //* NO-OP *//*
                    }
                }
            }
        }
    }

    private fun connectDialog(context: Context) {
        val uwbListener = UWBListener(viewModel.repository)
        val pairedDevices = uwbListener.getPairedDevices()
        var selectedDevice: BluetoothDevice? = pairedDevices.firstOrNull()
        viewLifecycleOwner.lifecycleScope.launch {
            uwbListener.connectionMessage.collect {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                if (it.contains("Failed")) {
                    viewModel.uwbListener = null
                    binding.btnRecord.text = "Connect"
                }
            }
        }
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.paired))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                binding.btnRecord.text = "Connect"
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                selectedDevice?.let {
                    viewModel.uwbListener = uwbListener
                    lifecycleScope.launch {
                        viewModel.uwbListener!!.startCommunication(it)
                    }
                }
            }
            .setSingleChoiceItems(pairedDevices.map { it.name } .toTypedArray(), 0) { dialog, which ->
                selectedDevice = pairedDevices[which]
            }
            .show()
    }
}