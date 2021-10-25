package com.takumi.equalizersampleapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import com.takumi.equalizersampleapp.R
import com.takumi.equalizersampleapp.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {

    @Inject
    lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPlayerBinding.bind(view)
        binding.apply {
            playerView.player = simpleExoPlayer

            buttonEqualizer.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, EqualizerFragment())
                    .addToBackStack(this@PlayerFragment::class.java.simpleName)
                    .commit()
            }
        }
    }
}