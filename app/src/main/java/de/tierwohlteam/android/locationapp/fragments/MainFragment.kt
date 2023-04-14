package de.tierwohlteam.android.locationapp.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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
        val animator = ValueAnimator.ofFloat(0f, 1f)

        binding.btnReplay.setOnClickListener {
            val startX = 100f
            val startY = 100f
            val endX = 250f
            val endY = 500f
            animator.apply {
                setFloatValues(0f, 1f)
                duration = 1000
                addUpdateListener { animation ->
                    val fraction = animation.animatedFraction
                    val x = startX + (endX - startX) * fraction
                    val y = startY + (endY - startY) * fraction
                    binding.circleView.setCircle(x, y,50f)
                    Log.d("CIRCLE", "Moving $x, $y")
                }
                start()
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