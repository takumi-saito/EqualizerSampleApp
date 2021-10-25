package com.takumi.equalizersampleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.takumi.equalizersampleapp.R
import com.takumi.equalizersampleapp.databinding.ItemEqualizerBinding
import com.takumi.equalizersampleapp.ui.BindingViewHolder
import com.takumi.equalizersampleapp.ui.VerticalSeekBar
import com.takumi.equalizersampleapp.viewmodel.ItemEqualizerViewModel

class EqualizerAdapter(
    private val lifecycleOwner: LifecycleOwner,
    viewModelList: List<ItemEqualizerViewModel> = emptyList()
) : RecyclerView.Adapter<BindingViewHolder>() {

    private var viewModelList: MutableList<ItemEqualizerViewModel> = viewModelList.toMutableList()
        set(value) {
            updateListItem(value)
        }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_equalizer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        return BindingViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val viewModel = viewModelList[position]
        (holder.binding as ItemEqualizerBinding).apply {
            this.viewModel = viewModel
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    (seekBar as VerticalSeekBar).let {
                        if (seekBar.isFromUser) {
                            viewModel.notifyUpdateStopTrackingTouchSeekBar(seekBar.progress)
                        }
                    }
                    viewModel.notifyUpdateSeekProgress(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
        holder.binding.lifecycleOwner = lifecycleOwner
    }

    override fun getItemCount() = viewModelList.size

    fun updateListItem(newContents: List<ItemEqualizerViewModel>) {
        viewModelList.clear()
        viewModelList.addAll(newContents)
        notifyDataSetChanged()
    }
}
