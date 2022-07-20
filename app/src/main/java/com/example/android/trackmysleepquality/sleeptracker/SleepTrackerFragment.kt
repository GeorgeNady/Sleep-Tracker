/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    private val binding by lazy { FragmentSleepTrackerBinding.inflate(layoutInflater) }

    private val application by lazy { requireNotNull(activity).application }
    private val dataSource by lazy { SleepDatabase(application).sleepDatabaseDao() }
    private val viewModel by viewModels<SleepTrackerViewModel> {
        SleepTrackerViewModelFactory(dataSource, application)
    }

    private val adapter by lazy {
        SleepNightAdapter(SleepNightCLickLinter {
            viewModel.onNavigatingToDetails(it)
        })
    }

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get a reference to the binding object and inflate the fragment views.
        binding.apply {
            lifecycleOwner = this@SleepTrackerFragment
            bAdapter = adapter
            bViewModel = viewModel.also {
                it.viewModelObservers()
            }

            recyclerView.setupWithGridLayoutManager()
        }
        return binding.root
    }

    private fun RecyclerView.setupWithGridLayoutManager() {
        val gridLayoutManager = GridLayoutManager(requireContext(),3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 3
                else -> 1
            }
        }
        layoutManager = gridLayoutManager

    }

    private fun SleepTrackerViewModel.viewModelObservers() {
        navigateToSleepQuality.observe(viewLifecycleOwner) { sleepNight ->
            sleepNight?.let {
                findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(it.nightId)
                )
                doneNavigating()
            }
        }

        navigateToSleepDetails.observe(viewLifecycleOwner) { sleepNight ->
            sleepNight?.let {
                findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailsFragment(it.nightId)
                )
                doneNavigating()
            }
        }

        showSnackBarEvent.observe(viewLifecycleOwner) {
            it?.let { bool ->
                if (bool) showSnackBar(resources.getString(R.string.cleared_message))
            }
        }

        nights.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        }
    }


    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.root)
            .show()
    }
}
