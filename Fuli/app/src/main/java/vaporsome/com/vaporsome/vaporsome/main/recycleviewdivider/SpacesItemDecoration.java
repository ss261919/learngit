package vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider;

import android.graphics.Rect;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * PACKAGE_NAME: com.tomato.z.recyclerviewsyllabus.divider
 * FUNCTIONAL_DESCRIPTION:
 * CREATE_BY: 尽际
 * CREATE_TIME: 16/1/26 下午10:52
 * MODIFICATORY_DESCRIPTION:
 * MODIFY_BY:
 * MODIFICATORY_TIME:
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;

    public static final int VERTICAL = OrientationHelper.VERTICAL;

    private int space;
    private int ori;

    public SpacesItemDecoration(int space, int ori) {
        this.space = space;
        this.ori = ori;
    }public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if (parent.getChildAdapterPosition(view) == 0 && ori == VERTICAL) {
            outRect.top = space;
        }
    }
}

