package com.takumi.equalizersampleapp.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var isVerticalScrollEnabled = true
    private var isHorizontalScrollEnabled = true
    fun setScrollEnabled(v: Boolean, h: Boolean) {
        isVerticalScrollEnabled = v
        isHorizontalScrollEnabled = h
    }

    override fun canScrollVertically(): Boolean {
        return isVerticalScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isHorizontalScrollEnabled && super.canScrollHorizontally()
    }
}