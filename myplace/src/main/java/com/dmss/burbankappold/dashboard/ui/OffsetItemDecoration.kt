package com.dmss.burbankappold.dashboard.ui

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecoration constructor(
    @DimenRes private val spacing: Int,
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val itemPosition: Int = parent.getChildAdapterPosition(view)
            if (itemPosition == RecyclerView.NO_POSITION) return

            val spacingPixelSize: Int = parent.context.resources.getDimensionPixelSize(spacing)

            when (itemPosition) {
                0 ->
                    outRect.set(getOffsetPixelSize(parent, view), 0, spacingPixelSize / 2, 0)
                parent.adapter!!.itemCount - 1 ->
                    outRect.set(spacingPixelSize / 2, 0, getOffsetPixelSize(parent, view), 0)
                else ->
                    outRect.set(spacingPixelSize / 2, 0, spacingPixelSize / 2, 0)
            }
        }

        private fun getOffsetPixelSize(parent: RecyclerView, view: View): Int {
            val orientationHelper = OrientationHelper.createHorizontalHelper(parent.layoutManager)
            return (orientationHelper.totalSpace - view.layoutParams.width) / 2
        }
    }