package com.example.playlistmaker.media.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlaylistGridSpacingDecoration(
    private val horizontalSpacingPx: Int,
    private val verticalSpacingPx: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = layoutParams.spanIndex
        val spanCount = (parent.layoutManager as GridLayoutManager).spanCount

        val halfSpacing = horizontalSpacingPx / 2
        when (spanIndex) {
            0 -> outRect.right = halfSpacing
            spanCount - 1 -> outRect.left = halfSpacing
        }


        val position = parent.getChildAdapterPosition(view)
        val isFirstRow = position < spanCount
        outRect.top = if (isFirstRow) 0 else verticalSpacingPx

        val itemCount = state.itemCount

    }
}
