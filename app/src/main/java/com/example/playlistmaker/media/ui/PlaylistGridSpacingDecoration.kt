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

        // Горизонтальные отступы (между колонками)
        val halfSpacing = horizontalSpacingPx / 2
        when (spanIndex) {
            0 -> outRect.right = halfSpacing  // Левая колонка
            spanCount - 1 -> outRect.left = halfSpacing  // Правая колонка
        }


        // Вертикальные отступы
        val position = parent.getChildAdapterPosition(view)
        val isFirstRow = position < spanCount
        outRect.top = if (isFirstRow) 0 else verticalSpacingPx

        // Опционально: нижний отступ для последней строки
        val itemCount = state.itemCount

    }
}
