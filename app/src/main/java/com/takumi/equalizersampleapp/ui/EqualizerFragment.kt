package com.takumi.equalizersampleapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.takumi.equalizersampleapp.adapter.EqualizerAdapter
import com.takumi.equalizersampleapp.viewmodel.EqualizerViewModel
import com.takumi.equalizersampleapp.data.EventObserver
import com.takumi.equalizersampleapp.viewmodel.ItemEqualizerViewModel
import com.takumi.equalizersampleapp.viewmodel.ItemPresetViewModel
import com.takumi.equalizersampleapp.adapter.PresetAdapter
import com.takumi.equalizersampleapp.R
import com.takumi.equalizersampleapp.data.Preset
import com.takumi.equalizersampleapp.databinding.FragmentEqualizerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EqualizerFragment : Fragment(R.layout.fragment_equalizer) {

    @Inject
    lateinit var itemEqualizerViewModelFactory: ItemEqualizerViewModel.Factory
    @Inject
    lateinit var itemPresetViewModelFactory: ItemPresetViewModel.Factory

    private val equalizerViewModel by viewModels<EqualizerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEqualizerBinding.bind(view)

        binding.apply {
            recyclerView.layoutManager = CustomLinearLayoutManager(requireActivity().applicationContext).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
                it.setScrollEnabled(v = false, h = false)
            }
            val adapter = EqualizerAdapter(lifecycleOwner = this@EqualizerFragment.viewLifecycleOwner)
            recyclerView.adapter = adapter

            equalizerViewModel.equalizerData.observe(this@EqualizerFragment.viewLifecycleOwner) { equalizerData ->
                equalizerData.bandDataList.map { bandData ->
                    val itemEqualizerViewModel = ViewModelProviders.of(
                        this@EqualizerFragment,
                        ItemEqualizerViewModel.provideFactory(
                            equalizerData.maxEQLevel,
                            equalizerData.minEQLevel,
                            bandData.band,
                            bandData.bandLevel,
                            bandData.centerFreq,
                            itemEqualizerViewModelFactory,
                        )
                    ).get(
                        "${bandData.band}",
                        ItemEqualizerViewModel::class.java
                    )
                    itemEqualizerViewModel.notifyUpdateProgress(bandData.bandLevel)
                    subscribeToNavigationChanges(itemEqualizerViewModel)
                    itemEqualizerViewModel
                }.let {
                    adapter.updateListItem(it)
                }
            }

            val presetAdapter = PresetAdapter(
                lifecycleOwner = this@EqualizerFragment.viewLifecycleOwner
            )
            rvPreset.adapter = presetAdapter
            equalizerViewModel.presetList.observe(this@EqualizerFragment.viewLifecycleOwner) { presetList ->
                presetList.map {
                    val itemPresetViewModel = ViewModelProviders.of(
                        this@EqualizerFragment,
                        ItemPresetViewModel.provideFactory(
                            it,
                            itemPresetViewModelFactory,
                        )
                    ).get(
                        "${it.presetName}",
                        ItemPresetViewModel::class.java
                    )
                    subscribeToNavigationChanges(itemPresetViewModel)
                    itemPresetViewModel
                }.let {
                    presetAdapter.updateListItem(it)
                }
            }
            equalizerViewModel.currentPreset.observe(this@EqualizerFragment.viewLifecycleOwner) { preset->
                val itemPresetViewModel = ViewModelProviders.of(
                    this@EqualizerFragment,
                    ItemPresetViewModel.provideFactory(
                        preset,
                        itemPresetViewModelFactory,
                    )
                ).get(
                    preset.presetName,
                    ItemPresetViewModel::class.java
                )
                presetAdapter.updateSelectedItemPresetViewModel(itemPresetViewModel)
            }
        }

    }

    private fun subscribeToNavigationChanges(viewModel: ItemEqualizerViewModel) {
        viewModel.seekProgressPercent.observe(this@EqualizerFragment.viewLifecycleOwner) { progress ->
            equalizerViewModel.notifyUpdateBandLevel(viewModel.band, progress)
        }
        viewModel.stopTrackingTouchProgress.observe(this@EqualizerFragment.viewLifecycleOwner, EventObserver { progress ->
            equalizerViewModel.presetList.value?.first { it is Preset.Custom }
                ?.let {
                    equalizerViewModel.updateSettingsCurrentPreset(it)
                }
            equalizerViewModel.updateCustomBandData(viewModel.band, viewModel.centerFreq, progress)
        })
    }

    private fun subscribeToNavigationChanges(viewModel: ItemPresetViewModel) {
        viewModel.eventPreset.observe(this@EqualizerFragment.viewLifecycleOwner, EventObserver {
            equalizerViewModel.notifyUpdateBandLevel(it)
            equalizerViewModel.updateSettingsCurrentPreset(it)
            Toast.makeText(requireContext(), it.presetName, Toast.LENGTH_SHORT).show()
        })
    }
}