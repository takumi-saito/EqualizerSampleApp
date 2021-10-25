package com.takumi.equalizersampleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.takumi.equalizersampleapp.R
import com.takumi.equalizersampleapp.databinding.ItemPresetBinding
import com.takumi.equalizersampleapp.ui.BindingViewHolder
import com.takumi.equalizersampleapp.viewmodel.ItemPresetViewModel

class PresetAdapter(
    private val lifecycleOwner: LifecycleOwner,
    viewModelList: List<ItemPresetViewModel> = emptyList()
) : RecyclerView.Adapter<BindingViewHolder>() {
    private var viewModelList: MutableList<ItemPresetViewModel> = viewModelList.toMutableList()
        set(value) {
            updateListItem(value)
        }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_preset
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        return BindingViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val viewModel = viewModelList[position]
        (holder.binding as ItemPresetBinding).apply {
            this.viewModel = viewModel
        }
        holder.binding.lifecycleOwner = lifecycleOwner
    }

    override fun getItemCount() = viewModelList.size

    fun updateListItem(newContents: List<ItemPresetViewModel>) {
        viewModelList.clear()
        viewModelList.addAll(newContents)
        notifyDataSetChanged()
    }

    fun updateSelectedItemPresetViewModel(itemPresetViewModel: ItemPresetViewModel) {
        viewModelList.forEach {
            it.notifySelected(false)
        }
        itemPresetViewModel.notifySelected(true)
    }
}

