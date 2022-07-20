package com.example.android.trackmysleepquality.sleepdetailes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailsBinding
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding
import com.example.android.trackmysleepquality.sleepquality.SleepQualityViewModelFactory

class SleepDetailsFragment : Fragment() {

    private val binding by lazy { FragmentSleepDetailsBinding.inflate(layoutInflater) }

    private val args by navArgs<SleepDetailsFragmentArgs>()
    private val sleepNightId by lazy { args.sleepNightKey }

    private val application by lazy { requireNotNull(activity).application }
    private val dataSource by lazy { SleepDatabase(application).sleepDatabaseDao() }
    private val viewModel by viewModels<SleepDetailsViewModel> {
        SleepDetailsViewModelFactory(sleepNightId, dataSource)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.apply {
            lifecycleOwner = this@SleepDetailsFragment
            bViewModel = viewModel

        }
        return binding.root
    }

}