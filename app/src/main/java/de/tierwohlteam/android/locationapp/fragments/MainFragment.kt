package de.tierwohlteam.android.locationapp.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.locationapp.R
import de.tierwohlteam.android.locationapp.databinding.FragmentMainBinding
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

        binding.btnRecord.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.record()
            }
        }

        binding.imageView2.setImageResource(R.drawable.roedelsee)
        binding.circleView.bringToFront()

        binding.btnReplay.setOnClickListener {
            lifecycleScope.launch {
                val locationList = viewModel.getLocations().toMutableList()
                if (locationList.isNotEmpty()) {
                    val animatorList = mutableListOf<Animator>()
                    for (i in 0 until locationList.size - 1) {
                        val startPoint = locationList[i]
                        val endPoint = locationList[i + 1]
                        val animator = ValueAnimator.ofFloat(0f, 1f)
                        //val animator = ObjectAnimator.ofFloat(0f, 1f)
                        animator.duration = endPoint.timestamp - startPoint.timestamp // Set the duration of each animation
                        animator.addUpdateListener { valueAnimator ->
                            val fraction = valueAnimator.animatedFraction
                            val x = startPoint.x + fraction * (endPoint.x - startPoint.x)
                            val y = startPoint.y + fraction * (endPoint.y - startPoint.y)
                            // Update the position of the circle
                            binding.circleView.setCircle(x.toFloat(), y.toFloat(),50f)
                        }
                        animatorList.add(animator)
                    }
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
           viewModel.insertLocationFlow.collect {
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
                            "Locations gespeichert",
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
}