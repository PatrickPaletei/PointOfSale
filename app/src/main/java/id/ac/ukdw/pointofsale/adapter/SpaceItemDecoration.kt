package id.ac.ukdw.pointofsale.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int, private val spanCount: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = space - column * space / spanCount
        outRect.right = (column + 1) * space / spanCount

        if (position < spanCount) {
            outRect.top = space
        }
        outRect.bottom = space
    }
}